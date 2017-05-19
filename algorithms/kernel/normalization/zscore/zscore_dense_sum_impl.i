/* file: zscore_dense_sum_impl.i */
/*******************************************************************************
* Copyright 2014-2017 Intel Corporation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

/*
//++
//  Implementation of sumDense method for zscore normalization algorithm
//--
*/

#ifndef __ZSCORE_DENSE_SUM_IMPL_I__
#define __ZSCORE_DENSE_SUM_IMPL_I__

namespace daal
{
namespace algorithms
{
namespace normalization
{
namespace zscore
{
namespace internal
{


/* TLS structure with local arrays and variables */
template<typename algorithmFPType, CpuType cpu>
struct tls_data_t
{
    algorithmFPType* variance;

    tls_data_t(size_t nFeatures)
    {
        variance  = service_scalable_calloc<algorithmFPType, cpu>(nFeatures);
    }

    ~tls_data_t()
    {
        if(variance) { service_scalable_free<algorithmFPType,cpu>( variance ); variance = 0; }
    }
};


template<typename algorithmFPType, CpuType cpu>
Status ZScoreKernel<algorithmFPType, sumDense, cpu>::computeMeanVariance_thr(NumericTablePtr &inputTable,
                                                                             algorithmFPType* resultMean,
                                                                             algorithmFPType* resultVariance,
                                                                             const daal::algorithms::Parameter &parameter)
{
    const size_t _nVectors  = inputTable->getNumberOfRows();
    const size_t _nFeatures = inputTable->getNumberOfColumns();

    NumericTablePtr sumTable = inputTable->basicStatistics.get(NumericTableIface::sum);
    DAAL_CHECK(sumTable, ErrorPrecomputedSumNotAvailable);

    ReadRows<algorithmFPType, cpu, NumericTable> sumBlock( sumTable.get(), 0, 1 );
    const algorithmFPType* sumArray = sumBlock.get();

    algorithmFPType invN   = algorithmFPType(1.0)/algorithmFPType(_nVectors);
    algorithmFPType invNm1 = algorithmFPType(1.0)/( algorithmFPType(_nVectors) - algorithmFPType(1.0) );

    /* Compute means from sums */
   PRAGMA_IVDEP
   PRAGMA_VECTOR_ALWAYS
    for(int j = 0; j < _nFeatures; j++)
    {
        resultMean[j] = sumArray[j] * invN;
    }

#define _BLOCK_SIZE_ 256

    /* Split rows by blocks, block size cannot be less than _nVectors */
    size_t numRowsInBlock = (_nVectors > _BLOCK_SIZE_)?_BLOCK_SIZE_:_nVectors;
    /* Number of blocks */
    size_t numBlocks   = _nVectors / numRowsInBlock;
    /* Last block can be bigger than others */
    size_t numRowsInLastBlock = numRowsInBlock + ( _nVectors - numBlocks * numRowsInBlock);

    /* TLS data initialization */
    daal::tls<tls_data_t<algorithmFPType, cpu> *> tls_data([ & ]()
    {
        return new tls_data_t<algorithmFPType, cpu>( _nFeatures );
    });

    SafeStatus safeStat;
    /* Compute partial unscaled variances for each block */
    daal::threader_for( numBlocks, numBlocks, [ & ](int iBlock)
    {
        struct tls_data_t<algorithmFPType,cpu> * tls_data_local = tls_data.local();
        DAAL_CHECK_THR(tls_data_local->variance, ErrorMemoryAllocationFailed);

        size_t _nRows    = (iBlock < (numBlocks-1))?numRowsInBlock:numRowsInLastBlock;
        size_t _startRow = iBlock * numRowsInBlock;

        ReadRows<algorithmFPType, cpu, NumericTable> dataTableBD( *inputTable.get(), _startRow, _nRows );
        DAAL_CHECK_BLOCK_STATUS_THR(dataTableBD);
        const algorithmFPType* dataArray_local = dataTableBD.get();

        algorithmFPType* variance_local = tls_data_local->variance;

        for(int i = 0; i < _nRows; i++)
        {
           PRAGMA_IVDEP
           PRAGMA_VECTOR_ALWAYS
            for(int j = 0; j < _nFeatures; j++)
            {
                algorithmFPType _v = dataArray_local[i*_nFeatures + j] - resultMean[j];
                variance_local[j]  +=  (_v * _v);
            }
        }
    } );

    /* Merge unscaled variance arrays by blocks */
    tls_data.reduce( [ & ]( tls_data_t<algorithmFPType,cpu>* tls_data_local )
    {
        if(!tls_data_local->variance)
        {
            delete tls_data_local;
            return;
        }

        PRAGMA_IVDEP
        PRAGMA_VECTOR_ALWAYS
        for(int j = 0; j < _nFeatures; j++)
        {
            resultVariance[j]  += tls_data_local->variance[j] ;
        }

        delete tls_data_local;
    } );

    /* Convert array of variances to inverse sigma's */
   PRAGMA_IVDEP
   PRAGMA_VECTOR_ALWAYS
    for(int j = 0; j < _nFeatures; j++)
    {
        resultVariance[j] = algorithmFPType(1.0) / Math<algorithmFPType, cpu>::sSqrt(resultVariance[j] * invNm1);
    }

    return safeStat.detach();
}

} // namespace internal
} // namespace zscore
} // namespace normalization
} // namespace algorithms
} // namespace daal

#endif
