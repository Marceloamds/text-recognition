package com.jera.vision.presentation.view.ar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.jera.vision.R
import com.jera.vision.databinding.ActivityArBinding
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ARActivity : BaseActivity() {
    private lateinit var binding: ActivityArBinding
    override val baseViewModel: BaseViewModel get() = _viewModel
    private val _viewModel: ARViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ar)
        setupUi()
    }

    private var customRenderable: ModelRenderable? = null

    private fun setupUi() {
        ModelRenderable.builder()
            .setSource(
                this,
                R.raw.out
            )
            .setIsFilamentGltf(true)
            .build()
            .thenAccept {
                customRenderable = it
                customRenderable?.isShadowCaster = false
                customRenderable?.isShadowReceiver = false
            }
            .exceptionally {
                Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show()
                null
            }
        val arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        arFragment.arSceneView.isLightDirectionUpdateEnabled = false
        arFragment.arSceneView.isLightEstimationEnabled = false
        arFragment.arSceneView.planeRenderer.isShadowReceiver = false
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            if (customRenderable == null) {
                return@setOnTapArPlaneListener
            }

            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment.arSceneView.scene)
            TranslatableNode().apply {
                setParent(anchorNode)
                addOffset(0f, 2f, -5.2f)
                renderable = customRenderable
            }
        }
    }

    companion object {

        fun createIntent(context: Context) = Intent(context, ARActivity::class.java)
    }
}