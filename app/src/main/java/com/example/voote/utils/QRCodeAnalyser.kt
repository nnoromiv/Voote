package com.example.voote.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class QRCodeAnalyser (
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        TODO("Not yet implemented")
    }

}


//class QRCodeAnalyzer(
//    private val onQRCodeScanned: (String) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val scanner = BarcodeScanning.getClient()
//
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image ?: return
//        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//
//        scanner.process(inputImage)
//            .addOnSuccessListener { barcodes ->
//                for (barcode in barcodes) {
//                    barcode.rawValue?.let { onQRCodeScanned(it) }
//                }
//            }
//            .addOnFailureListener {
//                Log.e("QRCodeAnalyzer", "Scanning failed", it)
//            }
//            .addOnCompleteListener {
//                imageProxy.close()
//            }
//    }
//}
