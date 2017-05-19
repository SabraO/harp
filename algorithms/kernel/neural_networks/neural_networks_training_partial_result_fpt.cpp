/* file: neural_networks_training_partial_result_fpt.cpp */
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

#include "neural_networks_training_partial_result.h"

using namespace daal::data_management;
using namespace daal::services;

namespace daal
{
namespace algorithms
{
namespace neural_networks
{
namespace training
{

template<typename algorithmFPType>
DAAL_EXPORT Status PartialResult::allocate(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method)
{
    set(batchSize, NumericTablePtr(new HomogenNumericTable<algorithmFPType>(1, 1, NumericTableIface::doAllocate)));
    return Status();
}

template<typename algorithmFPType>
DAAL_EXPORT Status DistributedPartialResult::allocate(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method)
{
    return Status();
}

template DAAL_EXPORT Status PartialResult           ::allocate<DAAL_FPTYPE>(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method);
template DAAL_EXPORT Status DistributedPartialResult::allocate<DAAL_FPTYPE>(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method);

}
}
}
}
