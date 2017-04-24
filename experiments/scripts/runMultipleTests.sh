if [ $# != 2 ] ; then
echo "USAGE: $0 e/m/r/d/cl0 <dataset:miami/web-google/gnp1m100e>"
echo " e.g.: $0 e web-google"
exit 1;
fi

for t in {1..2}
do
	for temp in u5-1.t #u3-1.t u5-1.t u7-1.t # u5-2.t u5-3.t u7-1.t #u10-1.t
	do
		for i in 4 # 8 16 #2
		do
			./runHarpSgcr.sh $HARP_SAHAD_HOME/experiments/template/$temp sahad-data/$2/$2-u-$1-$i sahad-output/$2/$2-u-$1-$i/40threads/$t $i 40
			hdfs dfs -cat sahad-output/$2/$2-u-$1-$i/40threads/$t/*
        		echo "[The $t times] Above experiment is: ./runHarpSgcr.sh $HARP_SAHAD_HOME/experiments/template/$temp sahad-data/$2/$2-u-$1-$i sahad-output/$2/$2-u-$1-$i/40threads/$t $i 40"
		done
	done
done
