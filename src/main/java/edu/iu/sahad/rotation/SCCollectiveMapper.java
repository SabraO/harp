package edu.iu.sahad.rotation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.CollectiveMapper;

import edu.iu.harp.example.IntArrPlus;
import edu.iu.harp.partition.Partition;
import edu.iu.harp.partition.Table;
import edu.iu.harp.resource.IntArray;
import edu.iu.harp.schdynamic.DynamicScheduler;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

/*
 * use key-object; in-memory
 * Instead of using allgather, using rotation
 */
public class SCCollectiveMapper  extends CollectiveMapper<String, String, Object, Object> {
	private int numMappers;
	private int numColor;
	private int isom;
	private int sizeTemplate;
	private String template;
	private String wholeTemplateName;
	private ArrayList<SCSubJob> subjoblist;
	private Random rand = new Random();
	private int numMaxThreads;
	private int numThreads;
	boolean useLocalMultiThread;
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		LOG.info("start setup" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
		Configuration configuration = context.getConfiguration();
    	numMappers = configuration.getInt(SCConstants.NUM_MAPPERS, 10);
    	template = configuration.get(SCConstants.TEMPLATE_PATH);
    	useLocalMultiThread = configuration.getBoolean(SCConstants.USE_LOCAL_MULTITHREAD, true);
    	System.out.println("init template");
    	System.out.println(template);
    	numMaxThreads = Runtime.getRuntime().availableProcessors();
        
    	numThreads =configuration.getInt(SCConstants.NUM_THREADS_PER_NODE, 10); 
        if(numMaxThreads < numThreads){//if the numMaxThreads is less than numThreads, use numMaxThreads
        	numThreads = numMaxThreads;
        }
        System.out.println("numMaxTheads: "+numMaxThreads+";numThreads:"+numThreads);
		
    	init(template);
    	System.out.println("topologySort subjobs");
    	subjoblist = topologySort(subjoblist);
    	for(SCSubJob ssj:subjoblist){
    		System.out.println(ssj.toString());
    	}
	}
	
	private  Table<IntArray> readGraphDataMultiThread( Configuration conf, List<String> graphFiles){
			LOG.info("[BEGIN] SCCollectiveMapper.readGraphDataMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
			Table<IntArray> graphData = new Table<>(0, new IntArrPlus());
		 
		    List<GraphLoadTask> tasks = new LinkedList<>();
		    for (int i = 0; i < numThreads; i++) {
		    	tasks.add(new GraphLoadTask(conf));
		    }
		 
		    DynamicScheduler<String, ArrayList<Partition<IntArray>>, GraphLoadTask> compute 
		    	= new DynamicScheduler<>(tasks);
		    compute.start();
		    
		    for (String filename : graphFiles) {
		    	compute.submit(filename);
		    }
		  
		  
		    ArrayList<Partition<IntArray>> output=null;
		    while (compute.hasOutput()) {
		    	output = compute.waitForOutput();
		    	if(output != null){
		    		ArrayList<Partition<IntArray>> partialGraphDataList = output;
		    		for(Partition<IntArray> partialGraph:partialGraphDataList )
		    		{
		    			graphData.addPartition(partialGraph);
		    		}
		    	}
		    } 
		    compute.stop();
		    LOG.info("[END] SCCollectiveMapper.readGraphDataMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		 return graphData;
	}
	
	private  Table<IntArray> readGraphData( Configuration conf, List<String> graphFiles) throws IOException{
		 Table<IntArray> graphData = new Table<>(1, new IntArrPlus());
		 for (String file:graphFiles) {
			 Path pointFilePath = new Path(file);
			 FileSystem fs =pointFilePath.getFileSystem(conf);
			 FSDataInputStream in = fs.open(pointFilePath);
			 BufferedReader br  = new BufferedReader(new InputStreamReader(in));
			 try {
			      String line ="";
			      while((line=br.readLine())!=null){
			      String keyText = line.split("\t")[0];
			      String valueText = line.split("\t")[1];
			      
			      int key = Integer.parseInt(keyText.toString());
					 String[] itr = valueText.toString().split(",");
					 // the last one is " "; so discard it.
					 int length = itr.length-1;
					 //if the  length is zeor, it means that this vertex doesn't have any neighbors. So just skip it.
					 //-Ethan 03/17/2016
					 /*if(length == 0){
						 System.out.println("skip data:"+keyText.toString()+":"+valueText.toString()+";"+"length="+length);
						 continue;
					 }*/
					 int[] intValues = new int[length];
					 for(int i=0; i< length; i++){
						 intValues[i]= Integer.parseInt(itr[i]);
				     }
					 Partition<IntArray> partialgraph = new Partition<IntArray>(key, new IntArray(intValues, 0, length));
					 graphData.addPartition(partialgraph);
			      }
			  	} finally {
			      in.close();
			 }
			 
		 }
		 return graphData;
	}
    	
	protected void mapCollective( KeyValReader reader, Context context) throws IOException, InterruptedException {
		LOG.info("Start collective mapper:" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		System.out.println("Start collective mapper.");
		this.logMemUsage();
		LOG.info("Memory Used: "+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		
		List<String> graphFiles = new ArrayList<String>();
		System.out.println("load Graph data files");
		 
		long readGraphbegintime = System.currentTimeMillis();
		 
		while(reader.nextKeyValue()){
			String key = reader.getCurrentKey();
			String value =  reader.getCurrentValue();
			LOG.info("Key: " + key + ", Value: " + value);
			graphFiles.add(value);
		}
		 
		Configuration conf = context.getConfiguration();
		 
		//dataModelMap store all the intermediate result of computation
		Map<String, ColorCountPairsKVTable> dataModelMap = new HashMap<>();
		//graphData table stores the graph data
		Table<IntArray> graphData = new Table<>(2, new IntArrPlus());
		System.out.println("read Graph Data");
		
		if(useLocalMultiThread){
			graphData = readGraphDataMultiThread(conf, graphFiles);
		}else{
			graphData = readGraphData(conf, graphFiles);
		}
		
		 long readGraphendtime=System.currentTimeMillis();
		 System.out.println("read graph takes: "+(readGraphendtime- readGraphbegintime  )+"ms"); 
		 
		 System.out.println("loading data size:"+graphData.getNumPartitions());
		 LOG.info("after loading graph data");
		 this.logMemUsage();
		 LOG.info("Memory Used: "+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		 logGCTime();
		 //---------------main computation----------------------------------
		 for(SCSubJob subjob: subjoblist){
			 String subjobname = subjob.getSubJobID();
			 System.out.println("The subjob is: "+ subjobname);
			 if( subjobname.equals("i")){//color the graph, and then store the result in obj at Map<"i", obj>
				 
				 long coloringGraphbegintime = System.currentTimeMillis();
				 
				 ColorCountPairsKVTable coloredModel =null;
				 
				 if(useLocalMultiThread){
					 coloredModel = colorGraphMultiThread(graphData);
				 }else{
					 System.out.println("before colorGraph"+ subjobname);
					 coloredModel = colorGraph(graphData);
				 }

				 dataModelMap.put("i", coloredModel);
				 
				 LOG.info("after color the graph");
				 this.logMemUsage();
				 LOG.info("Memory Used: "+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
				 
				 long coloringGraphendtime = System.currentTimeMillis();
				 System.out.println("color graph takes: "+(coloringGraphendtime-coloringGraphbegintime  )+"ms"); 
				 logGCTime();	
			 }
			 else if(subjobname.equals("final")){// compute the final count, write back the result to HDFS
				// we don't need to use the whole table of the wholeMathcing result.So comment it.
				 /* String activeChild = subjob.getActiveChild();
				 ArrTable<IntArray> wholeMatchingTable = dataModelMap.get(activeChild);
				 int finalCount = finalCounting(wholeMatchingTable);
				 System.out.println("finalCount:"+finalCount);
				 */
				 continue;
			 }
			 else{// compute the intermediate process, namely sub-template matching
				 
				 long subjobbegintime = System.currentTimeMillis();
				 
				 ColorCountPairsKVTable subMatchingTable = null;
				 if(useLocalMultiThread){
					 subMatchingTable =  matchSubTemplateMultiThread(graphData, dataModelMap, subjob);
				 }else{
					 subMatchingTable =  matchSubTemplate(graphData, dataModelMap, subjob);
				 }
				 
				 long subjobendtime = System.currentTimeMillis();
				 System.out.println(subjobname+" is done."+"takes: "+ (subjobendtime- subjobbegintime ) +"ms");
				 
				 for(SCSubJob ssj: subjoblist){
					 if(ssj.getSubJobID().equals(subjob.getActiveChild())){
						 ssj.referedNum--;
					 }
					 if(ssj.getSubJobID().equals(subjob.getPassiveChild())){
						 ssj.referedNum--;
					 }
					 if(ssj.referedNum == 0 && dataModelMap.containsKey(ssj.getSubJobID())){//abolish
							 System.out.println("Before abolishing: Total Memory (bytes): " + " "
								      + Runtime.getRuntime().totalMemory()
								      + ", Free Memory (bytes): "
								      + Runtime.getRuntime().freeMemory());
							 
							 dataModelMap.remove(ssj.getSubJobID());
							 
							 System.out.println("Abolished table "+ssj.getSubJobID());
							 System.out.println("But I don't know if it works or not. Let's test it.");
							 System.out.println("After abolishing: Total Memory (bytes): " + " "
								      + Runtime.getRuntime().totalMemory()
								      + ", Free Memory (bytes): "
								      + Runtime.getRuntime().freeMemory());
						 }
				 }
				
				 if(subjobname.equals(wholeTemplateName)){
					 //If this is the whole template matching, then sum up the counts locally, then calculate the final result.
					 LOG.info("[BEGIN] SCCollectiveMapper.mapCollective.final: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
						
					 long wholetempbegintime = System.currentTimeMillis();

					 long localCount = localAggregate(subMatchingTable);
					 //do allgather to aggregate the final counting
					 ColorCountPairsKVTable localCountTable =  new ColorCountPairsKVTable(3);
					 
					 int key = -1;// -1 represents total counts, not a color.
					 
					 ColorCountPairs ccp = new ColorCountPairs();
					 ccp.addAPair(key, localCount);
					 localCountTable.addKeyVal(key, ccp);
					 
					 LOG.info("after subjob: "+subjobname);
					 this.logMemUsage();
					 LOG.info("Memory Used: "+
							 (Runtime.getRuntime().totalMemory() 
									 -Runtime.getRuntime().freeMemory()));
					 logGCTime();
					 
					 long allreducebegintime = System.currentTimeMillis();
					 
					 LOG.info("[BEGIN] SCCollectiveMapper.mapCollective.final.allreduce: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
					 allreduce(subjobname, "aggregate", localCountTable);
					 LOG.info("[END] SCCollectiveMapper.mapCollective.final.allreduce: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
					 
					 long allreduceendtime = System.currentTimeMillis();
					 System.out.println("allreduce is done."+"takes: "+ (allreduceendtime - allreducebegintime) +"ms");
					 
					 LOG.info("after subjob: "+subjobname+": allreduce");
					 this.logMemUsage();
					 
					 //get the aggregate result and then compute the final result
					 long finalCount=localCountTable.getVal(key).getCounts().get(0);
					 finalCount /= isom;
					 finalCount /= SCUtils.Prob(numColor, sizeTemplate);
					 
					 long wholetempendtime = System.currentTimeMillis();
					 System.out.println(subjobname+" is done."+"takes: "+ (wholetempendtime- wholetempbegintime ) +"ms");
					 logGCTime();	
					 
					 if(this.isMaster()){
						 context.write("finalCount", finalCount);
					 }
					 
					 LOG.info("[END] SCCollectiveMapper.mapCollective.final: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
						
				 }else{

					 dataModelMap.put(subjobname, subMatchingTable);
					 
					 LOG.info("after subjob: "+subjobname);
					 this.logMemUsage();
					 LOG.info("Memory Used: "+
							 (Runtime.getRuntime().totalMemory() 
									 -Runtime.getRuntime().freeMemory()));
					 logGCTime();
				 }
			 }
		 }
		//-------------------------------------------------------------------
	} 

	//local aggregate the counts
	private long localAggregate (ColorCountPairsKVTable subMatchingTable){
		long count = 0;
		for(int  parID: subMatchingTable.getPartitionIDs()){
			ColorCountPairs ccp = subMatchingTable.getVal(parID);
			for(int i = 0; i< ccp.getCounts().size(); i++){
				count +=  ccp.getCounts().get(i);
			}
		}
		return count;
	}
	
	//clone table
	private void cloneTable( ColorCountPairsKVTable curTable, ColorCountPairsKVTable newTable){

		for(Partition<ColorCountPairsKVPartition> par: curTable.getPartitions())
		{
			int key = par.id();
			ColorCountPairs ccp = par.get().getVal(key);
			ColorCountPairs newccp = new ColorCountPairs();
			newTable.addKeyVal(key, newccp);
		}
	}
		
	//subtemplate matching
	//using rotation model
	private ColorCountPairsKVTable matchSubTemplate(Table<IntArray> graphData, Map<String, ColorCountPairsKVTable> dataModelMap, SCSubJob subjob){
		ColorCountPairsKVTable modelTable =new ColorCountPairsKVTable(4);
		
		ColorCountPairsKVTable passiveChild = dataModelMap.get(subjob.getPassiveChild());
		ColorCountPairsKVTable activeChild;
		if(subjob.getActiveChild().equals(subjob.getPassiveChild())){
			//if the active and passive children are the same one
			//clone an activeChild
			activeChild = new ColorCountPairsKVTable(5);
			cloneTable(dataModelMap.get(subjob.getActiveChild()),activeChild);
			//this activeChild won't do rotation
			
		}else{
			activeChild = dataModelMap.get(subjob.getActiveChild());
		}
		
		Map<Integer, Long> colorCountMap;
		
		int numWorkers = this.getNumWorkers();
		System.out.println("numWorkers: "+numWorkers);
		int rotationNo = 0;
		
		do{//do rotation
			
		for(Partition<IntArray> graphpar: graphData.getPartitions())
		 {
			 int key =graphpar.id();
			 //get valuepairlist from activeChild
			 ColorCountPairs  activeValuelist = activeChild.getVal(key);
			 ColorCountPairs passiveValuelist = null;
			 colorCountMap = new HashMap<Integer, Long>();
			 
			 for(int i = graphpar.get().start(); i<graphpar.get().size(); i++){
				int neighbor = graphpar.get().get()[i];
				
				//get valuepairlist form passiveChild
				passiveValuelist = passiveChild.getVal(neighbor);
				
				if(passiveValuelist == null){
					continue;
				}
				
				
				if(activeValuelist==null){
					System.out.println("activeValuelist is null for key = "+key);
				}
				if(activeValuelist.getColors()==null){
					System.out.println("activeValuelist.getColors is null for key = "+key);
				}
				
				//compute the new result using these two valuepairlist
				for(int j = 0; j < activeValuelist.getColors().size(); j++){

					int activeColor =activeValuelist.getColors().get(j);
		            long activeCount = activeValuelist.getCounts().get(j);
					for(int k = 0; k < passiveValuelist.getColors().size(); k++){

						 int passiveColor =  passiveValuelist.getColors().get(k);
		                 long passiveCount =passiveValuelist.getCounts().get(k);
		              //  System.out.println("color:"+activeColor +":"+activeCount+":"+passiveColor+":"+passiveCount);
		                 if ((activeColor & passiveColor) == 0)
		                  // color set intersection is empty
		                  {
		                	 int newColor = activeColor | passiveColor;
		                     if (!colorCountMap.containsKey(newColor)) {
		                            colorCountMap.put(new Integer(newColor), 
		                            		new Long(activeCount * passiveCount));
		                     } else {
		                            Long count = colorCountMap.get(newColor);
		                            count += activeCount * passiveCount;
		                            colorCountMap.put(new Integer(newColor),
		                                    new Long(count));
		                        }
		                    }
					}
				}
			 }
			 
			 ColorCountPairs newccp = new ColorCountPairs();
			 for (int color : colorCountMap.keySet()) {
				 Long count = colorCountMap.get(color);
				 newccp.addAPair(color, count);
			 }
				 modelTable.addKeyVal(key, newccp);//combine
			 }
		
		rotate (subjob.getSubJobID(),"rotation"+rotationNo, passiveChild, null);
		rotationNo++;
		System.out.println("rotationNo:"+rotationNo);
		}while(rotationNo < numWorkers);//will eventually rotate to the original distribution
		
		return modelTable;
	}
	
	//subtemplate matching in MultiThread way
	private ColorCountPairsKVTable matchSubTemplateMultiThread(Table<IntArray> graphData, Map<String, ColorCountPairsKVTable> dataModelMap, SCSubJob subjob){
		LOG.info("[BEGIN] SCCollectiveMapper.matchSubTemplateMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
		ColorCountPairsKVTable modelTable = new ColorCountPairsKVTable(6);
		ColorCountPairsKVTable passiveChild = dataModelMap.get(subjob.getPassiveChild());
		ColorCountPairsKVTable activeChild;
		if(subjob.getActiveChild().equals(subjob.getPassiveChild())){
			//if the active and passive children are the same one
			//clone an activeChild
			activeChild = new ColorCountPairsKVTable(7);
			cloneTable(dataModelMap.get(subjob.getActiveChild()),activeChild);
			//this activeChild won't do rotation
		}else{
			activeChild = dataModelMap.get(subjob.getActiveChild());
		}
		
		int numWorkers = this.getNumWorkers();
		int rotationNo = 0;
		System.out.println("numWorkers: "+numWorkers+"; numMaxTheads: "+numMaxThreads+";numThreads:"+numThreads);
		do{//do rotation
			LOG.info("[BEGIN] SCCollectiveMapper.matchSubTemplateMultiThread. Computation " +rotationNo+" :" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
			long computationbegintime = System.currentTimeMillis();
			List<SubMatchingTask> tasks = new LinkedList<>();
			for (int i = 0; i < numThreads; i++) {
				tasks.add(new SubMatchingTask(graphData,passiveChild ));
			}
			// doTasks(cenPartitions, output, tasks);
			DynamicScheduler<Partition<ColorCountPairsKVPartition>, Map<Integer, ColorCountPairs>, SubMatchingTask> 
				compute = new DynamicScheduler<>(tasks);
			compute.start();
			
			for (Partition<ColorCountPairsKVPartition> partition : activeChild.getPartitions()) {
	    		compute.submit(partition);
			}
		
			Map<Integer, ColorCountPairs> output=null;
			while(compute.hasOutput()){
				output = compute.waitForOutput();
				if(output != null){ 
						Map<Integer, ColorCountPairs> outmp   = (Map<Integer, ColorCountPairs>) output;
						for(int key: outmp.keySet()){
							modelTable.addKeyVal(key, outmp.get(key));
						}
				}
			} 
			compute.stop();
			
			long computationendtime = System.currentTimeMillis();
			LOG.info("[END] SCCollectiveMapper.matchSubTemplateMultiThread. Computation " +rotationNo+"; it takes: "+(computationendtime - computationbegintime)+"ms");
			long rotationbegintime = System.currentTimeMillis();
			LOG.info("[BEGIN] SCCollectiveMapper.matchSubTemplateMultiThread. Rotation " +rotationNo);
			rotate(subjob.getSubJobID(),"rotation"+rotationNo, passiveChild, null);
			long rotationendtime = System.currentTimeMillis();
			System.out.println(subjob.getSubJobID() +": rotation_"+rotationNo+"takes: "+(rotationendtime - rotationbegintime  )+"ms");
			LOG.info("[END] SCCollectiveMapper.matchSubTemplateMultiThread. Rotation " +rotationNo+"; it takes: "+(rotationendtime - rotationbegintime  )+"ms");
			
			rotationNo++;
			System.out.println("rotation "+rotationNo);
		
		}while(rotationNo < numWorkers);//will eventually rotate to the original distribution
		
		LOG.info("[END] SCCollectiveMapper.matchSubTemplateMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
		return modelTable;
	}
	
	
	//color the graph in MultiThread way
	private  ColorCountPairsKVTable colorGraphMultiThread( Table<IntArray> graphData ){
		LOG.info("[BEGIN] SCCollectiveMapper.colorGraphMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
		ColorCountPairsKVTable colorTable =  new ColorCountPairsKVTable(9);
		
		Collection<Partition<IntArray>> graphPartitions = graphData.getPartitions();
	    List<ColorTask> tasks = new LinkedList<>();
	    for (int i = 0; i < numThreads; i++) {
	    	tasks.add(new ColorTask(numColor, rand));
	    }
	    // doTasks(cenPartitions, output, tasks);
	    DynamicScheduler<Partition<IntArray>,  Map<Integer, ColorCountPairs>, ColorTask> 
	    	compute = new DynamicScheduler<>(tasks);
	    compute.start();
	    
	    for (Partition<IntArray> partition : graphPartitions) {
	    	compute.submit(partition);
	    }
	   
	 
	    Map<Integer, ColorCountPairs> output=null;
	    while(compute.hasOutput()){
	    	output = compute.waitForOutput();
	    	if(output != null){
	    		Map<Integer, ColorCountPairs> outmp   = output;
	    		for(int key: outmp.keySet()){
	    			colorTable.addKeyVal(key, outmp.get(key));
	    		}
	    	}
	    } 
	    compute.stop();
		
	    LOG.info("[END] SCCollectiveMapper.colorGraphMultiThread: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		
		return colorTable;
	}
	
	//color the graph
	private ColorCountPairsKVTable colorGraph( Table<IntArray> graphData ){
		ColorCountPairsKVTable table = new ColorCountPairsKVTable(8);
		for(int ID: graphData.getPartitionIDs()){
			//System.out.println("for ID:=" + ID);
			//int colorBit = SCUtils.power(2, rand.nextInt(numColor));
			int colorBit = SCUtils.power(2, ID % numColor );
			ColorCountPairs ccp = new ColorCountPairs();
			ccp.addAPair(colorBit, 1);
			table.addKeyVal(ID, ccp);
		}
		return table;
	}
	
	//load template
	private void init(String template){
		subjoblist = new ArrayList<SCSubJob>();
		try {
            String line;
            File f = new File(template);
            BufferedReader fReader = new BufferedReader(new FileReader(f));
            while ((line = fReader.readLine()) != null) {
                String[] st = line.split(" ");
                if(st.length<=1){
                	continue;
                }
                if (st[0].contains("final")) { // total count
                	// like final u5-1 5 2
                	SCSubJob subJob = new SCSubJob();
                	isom = Integer.parseInt(st[3]);
                	sizeTemplate = Integer.parseInt(st[2]);
                	subJob.setSubJobID(st[0]);
                	subJob.setActiveChild(st[1]);
                	wholeTemplateName = st[1];
                	subJob.setPassiveChild(null);
                    subjoblist.add(subJob);
                    
                	// new a subjob, or update an exsiting subjob
                	boolean flag=false;
                	for(SCSubJob ssj: subjoblist){
                		if (ssj.getSubJobID().equals(st[1])){
                			ssj.referedNum ++;
                			flag=true;
                			break;
                		}
                	}
                	if(flag==false){
                		SCSubJob ssj = new SCSubJob();
                		ssj.setSubJobID(st[1]);
                		ssj.referedNum ++;
                		subjoblist.add(ssj);
                	}
                	
                	

                } else if (st[0].equals("i")) { // random coloring
                	// like i graph 5
                    numColor = Integer.parseInt(st[2]);
                    
                    boolean flag=false;
                	for(SCSubJob ssj: subjoblist){
                		if (ssj.getSubJobID().equals(st[0])){
                			flag=true;
                			ssj.setActiveChild(null);
                			ssj.setPassiveChild(null);
                			break;
                		}
                	}
                	if(flag==false){
                		SCSubJob subJob = new SCSubJob();
                        subJob.setSubJobID(st[0]);
                        subJob.setActiveChild(null);
                    	subJob.setPassiveChild(null);
                    	 subjoblist.add(subJob);
                	}
                   
                } else {
                	// like u5-1 u3-1 u2
                    String activeChild = st[1];
                    String passiveChild = st[2];
                    
                    boolean flag=false;
                	for(SCSubJob ssj: subjoblist){
                		if (ssj.getSubJobID().equals(st[0])){
                			flag=true;
                			ssj.setActiveChild(activeChild);
                			ssj.setPassiveChild(passiveChild);
                			break;
                		}
                	}
                	if(flag==false){
                		  SCSubJob subJob = new SCSubJob();
                          subJob.setSubJobID(st[0]);
                          subJob.setActiveChild(activeChild);
                          subJob.setPassiveChild(passiveChild);
                          subjoblist.add(subJob);
                	}
                    
                	flag=false;
                	for(SCSubJob ssj: subjoblist){
                		if (ssj.getSubJobID().equals(activeChild)){
                			flag=true;
                			ssj.referedNum++;
                			break;
                		}
                	}
                	if(flag==false){
                		  SCSubJob subJob = new SCSubJob();
                          subJob.setSubJobID(activeChild);
                          subJob.referedNum++;
                          subjoblist.add(subJob);
                	}
                    
                	flag=false;
                	for(SCSubJob ssj: subjoblist){
                		if (ssj.getSubJobID().equals(passiveChild)){
                			flag=true;
                			ssj.referedNum++;
                			break;
                		}
                	}
                	if(flag==false){
                		  SCSubJob subJob = new SCSubJob();
                          subJob.setSubJobID(passiveChild);
                          subJob.referedNum++;
                          subjoblist.add(subJob);
                	}
                }
            }
            fReader.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private ArrayList<SCSubJob> topologySort(ArrayList<SCSubJob> subjoblist){
		ArrayList<SCSubJob> res = new ArrayList<SCSubJob>();
		Set<String> uniqueSet = new HashSet<String>();
		int size =  subjoblist.size();
		for(int j=0; j<size; j++){
			for(int i = 0; i < size; i++){
				SCSubJob scsjob = subjoblist.get(i);
				if(!uniqueSet.contains(scsjob.getSubJobID())){
					if( (scsjob.getActiveChild() == null || uniqueSet.contains(scsjob.getActiveChild()))
							&& 
						((scsjob.getPassiveChild() == null) ||  uniqueSet.contains(scsjob.getPassiveChild())) ) {
						res.add(scsjob);
						uniqueSet.add(scsjob.getSubJobID());
					}
				}
			}
		}
		return res;
	}

	//final counting
	private int finalCounting(Table<IntArray> wholeMatchingTable){
		int count = 0;
		for(Partition<IntArray> parWholeMatching: wholeMatchingTable.getPartitions()){
			for(int i = parWholeMatching.get().start()+1; i<parWholeMatching.get().size(); i+=2){
				count += parWholeMatching.get().get()[i];
			}
		}

		count /= isom;
		count /= SCUtils.Prob(numColor, sizeTemplate);
		
		
		return count;
	}
	
	//print for debugging
	private void printTable( ColorCountPairsKVTable table){
		for(Partition<ColorCountPairsKVPartition> par: table.getPartitions())
		 {
			int key = par.id();
			 ColorCountPairs ccp = par.get().getVal(key);
			 System.out.print(key+"\t");
			 for(int i = 0; i<ccp.getColors().size(); i++)
				 System.out.print(ccp.getColors().get(i)+","+ccp.getCounts().get(i)+",");
			 System.out.println();
		}
	}
}