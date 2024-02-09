package com.application.parkpilot.module

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.application.parkpilot.R
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.style.Color
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape

class QRGenerator(context: Context) {
    // for detail see here https://github.com/alexzhirkevich/custom-qr-generator?tab=readme-ov-file

    private val options = createQrVectorOptions {

        padding = .125f

//        background {
//            drawable = AppCompatResources.getDrawable(context, R.drawable.car_parking)
//        }

        logo {
            drawable = AppCompatResources.getDrawable(context, R.drawable.google_icon)
            size = .25f
            padding = QrVectorLogoPadding.Natural(.2f)
            shape = QrVectorLogoShape.Circle
        }
        colors {
            dark = QrVectorColor
                .Solid(Color(0xff345288))
            ball = QrVectorColor.Solid(
                context.getColor(R.color.black)
            )
            frame = QrVectorColor.LinearGradient(
                colors = listOf(
                    0f to Color.RED,
                    1f to Color.BLUE,
                ),
                orientation = QrVectorColor.LinearGradient
                    .Orientation.LeftDiagonal
            )
        }
        shapes {
            darkPixel = QrVectorPixelShape
                .RoundCorners(.5f)
            ball = QrVectorBallShape
                .RoundCorners(.25f)
            frame = QrVectorFrameShape
                .RoundCorners(.25f)
        }
    }

    fun generate(data: String): Drawable {
        return QrCodeDrawable(QrData.Text(data), options)
    }
}