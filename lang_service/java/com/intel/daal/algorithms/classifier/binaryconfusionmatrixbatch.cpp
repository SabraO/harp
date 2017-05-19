/* file: binaryconfusionmatrixbatch.cpp */
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

/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include "daal.h"
#include "classifier/quality_metric/binary_confusion_matrix/JBinaryConfusionMatrixBatch.h"
#include "classifier/quality_metric/binary_confusion_matrix/JBinaryConfusionMatrixMethod.h"

#include "common_helpers.h"

USING_COMMON_NAMESPACES();
using namespace daal::algorithms::classifier::quality_metric;
using namespace daal::algorithms::classifier::quality_metric::binary_confusion_matrix;

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cInit
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cInit
  (JNIEnv *, jobject, jint prec, jint method)
{
    return jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::newObj(prec, method);
}

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cSetResult
 * Signature: (JIIJ)V
 */
JNIEXPORT void JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cSetResult
  (JNIEnv *, jobject, jlong algAddr, jint prec, jint method, jlong resultAddr)
{
    jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::
        setResult<binary_confusion_matrix::Result>(prec, method, algAddr, resultAddr);
}

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cInitParameter
 * Signature: (JII)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cInitParameter
  (JNIEnv *, jobject, jlong algAddr, jint prec, jint method)
{
    return jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::getParameter(prec, method, algAddr);
}

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cGetInput
 * Signature: (JII)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cGetInput
  (JNIEnv *, jobject, jlong algAddr, jint prec, jint method)
{
    return jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::getInput(prec, method, algAddr);
}

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cGetResult
 * Signature: (JII)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cGetResult
  (JNIEnv *, jobject, jlong algAddr, jint prec, jint method)
{
    return jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::getResult(prec, method, algAddr);
}

/*
 * Class:     com_intel_daal_algorithms_classifier_quality_metric_binary_confusion_matrix_BinaryConfusionMatrixBatch
 * Method:    cClone
 * Signature: (JII)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_daal_algorithms_classifier_quality_1metric_binary_1confusion_1matrix_BinaryConfusionMatrixBatch_cClone
  (JNIEnv *, jobject, jlong algAddr, jint prec, jint method)
{
    return jniBatch<binary_confusion_matrix::Method, Batch, defaultDense>::getClone(prec, method, algAddr);
}
