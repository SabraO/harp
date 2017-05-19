/* file: sorting_fpt.cpp */
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
//  Implementation of sorting algorithm and types methods.
//--
*/

#include "sorting_types.h"

using namespace daal::data_management;

namespace daal
{
namespace algorithms
{
namespace sorting
{
namespace interface1
{
/**
 * Allocates memory to store final results of the sorting algorithms
 * \param[in] input     Input objects for the sorting algorithm
 * \param[in] method    Algorithm computation method
 */
template <typename algorithmFPType>
DAAL_EXPORT services::Status Result::allocate(const daal::algorithms::Input *input, const int method)
{
    const Input *in = static_cast<const Input *>(input);

    const size_t nFeatures = in->get(data)->getNumberOfColumns();
    const size_t nVectors = in->get(data)->getNumberOfRows();

    Argument::set(sortedData, NumericTablePtr(new HomogenNumericTable<algorithmFPType>(nFeatures, nVectors, NumericTable::doAllocate)));
    return services::Status();
}

template DAAL_EXPORT services::Status Result::allocate<DAAL_FPTYPE>(const daal::algorithms::Input *input, const int method);

}// namespace interface1
}// namespace sorting
}// namespace algorithms
}// namespace daal
