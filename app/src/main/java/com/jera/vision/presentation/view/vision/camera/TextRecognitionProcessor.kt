/*
 * Copyright 2020 Google LLC. All rights reserved.
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
 */

package com.jera.vision.presentation.view.vision.camera

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import java.text.Normalizer

class TextRecognitionProcessor(
    context: Context,
    private val expectedStringsList: List<String>
) : VisionProcessorBase<Text>(context) {
    private val textRecognizer: TextRecognizer = TextRecognition.getClient()

    override fun stop() {
        super.stop()
        textRecognizer.close()
    }

    override fun detectInImage(image: InputImage): Task<Text> {
        return textRecognizer.process(image)
    }

    override fun onSuccess(results: Text, graphicOverlay: GraphicOverlay) {
        results.textBlocks.forEach {
            it.lines.forEach {
                it.elements.forEach {
                    if (expectedStringsList.contains(
                            it.text.decapitalize().removePunctuation()
                        )
                    ) graphicOverlay.add(
                        TextGraphic(
                            graphicOverlay,
                            it
                        )
                    )
                }
            }
        }
    }

    private fun String.removePunctuation(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD).replace("[^\\p{ASCII}]", "")
    }

    override fun onFailure(e: Exception) {
        e.printStackTrace()
    }
}
