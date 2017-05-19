/* file: tanh_dense_default_impl.i */
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
//  Implementation of hyperbolic tangent algorithm
//--
*/

namespace daal
{
namespace algorithms
{
namespace math
{
namespace tanh
{
namespace internal
{

template<typename algorithmFPType, CpuType cpu>
inline Status TanhKernel<algorithmFPType, defaultDense, cpu>::processBlock(const NumericTable &inputTable, size_t nInputColumns,
                                                                           size_t nProcessedRows, size_t nRowsInCurrentBlock,
                                                                           NumericTable &resultTable)
{
    ReadRows<algorithmFPType, cpu, NumericTable> inputBlock(const_cast<NumericTable *>(&inputTable), nProcessedRows, nRowsInCurrentBlock);
    DAAL_CHECK_BLOCK_STATUS(inputBlock);
    const algorithmFPType* inputArray = inputBlock.get();

    WriteRows<algorithmFPType, cpu, NumericTable> resultBlock(&resultTable, nProcessedRows, nRowsInCurrentBlock);
    DAAL_CHECK_BLOCK_STATUS(resultBlock);
    algorithmFPType* resultArray = resultBlock.get();

    size_t nDataElements = nInputColumns * nRowsInCurrentBlock;
    daal::internal::Math<algorithmFPType,cpu>::vTanh(nDataElements, const_cast<algorithmFPType *>(inputArray), resultArray);
    return Status();
}

} // namespace daal::internal
} // namespace tanh
} // namespace math
} // namespace algorithms
} // namespace daal
