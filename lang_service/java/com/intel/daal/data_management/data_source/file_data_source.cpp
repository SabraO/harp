/* file: file_data_source.cpp */
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

#include "JFileDataSource.h"

#include "file_data_source.h"
#include "csv_feature_manager.h"
#include "common_helpers_functions.h"

using namespace daal;
using namespace daal::data_management;

/*
 * Class:     com_intel_daal_data_management_data_source_FileDataSource
 * Method:    cInit
 * Signature:(Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_data_1management_data_1source_FileDataSource_cInit
(JNIEnv *env, jobject obj, jstring jFileName)
{
    const char *fileName = env->GetStringUTFChars(jFileName, NULL);

    DataSource *ds = new FileDataSource<CSVFeatureManager>(fileName);
    env->ReleaseStringUTFChars(jFileName, fileName);
    if(!ds->status())
    {
        const services::Status s = ds->status();
        delete ds;
        DAAL_CHECK_THROW(s);
        return (jlong)0;
    }

    return(jlong)(ds);
}
