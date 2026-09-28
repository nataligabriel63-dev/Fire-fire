package com.example.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class FloatingCrosshairService : Service() {

    private var windowManager: WindowManager? = null
    private var crosshairView: CrosshairOverlayView? = null
    private var params: WindowManager.LayoutParams? = null

    companion object {
        const val ACTION_START = "ACTION_CROSSHAIR_START"
        const val ACTION_STOP = "ACTION_CROSSHAIR_STOP"
        const val ACTION_UPDATE = "ACTION_CROSSHAIR_UPDATE"

        var isRunning = false
            private set

        // Customization parameters:
        var crosshairStyle = 0 // 0: Tactical Dot, 1: Classic Esports Cross, 2: Circle + Center Dot, 3: Sniper T-Cross, 4: Triangle Chevrons
        var sizeDp = 32 // 16 to 70 dp
        var strokeWidthDp = 2.5f // 1 to 6 dp
        var gapSizeDp = 4f // 0 to 16 dp (space between lines)
        var lineLengthDp = 10f // 4 to 24 dp
        var opacityPercent = 90 // 20 to 100%
        var colorArgb = 0xFF00E5FF.toInt() // Cyan neon default
        var hasCenterDot = true
        var centerDotRadiusDp = 2f
        var hasOuterRing = false
        var isLocked = true // Touch through
        var offsetX = 0
        var offsetY = 0
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopCrosshair()
                stopSelf()
            }
            ACTION_UPDATE -> {
                updateCrosshair()
            }
            ACTION_START, null -> {
                startCrosshair()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCrosshair() {
        if (crosshairView != null) {
            updateCrosshair()
            return
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val flags = if (isLocked) {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        } else {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        val density = resources.displayMetrics.density
        val totalSizePx = ((sizeDp + 30) * density).toInt()

        params = WindowManager.LayoutParams(
            totalSizePx,
            totalSizePx,
            layoutType,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = offsetX
            y = offsetY
        }

        crosshairView = CrosshairOverlayView(this).apply {
            setOnTouchListener(object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (isLocked || event == null) return false
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params?.x ?: 0
                            initialY = params?.y ?: 0
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val newX = initialX + (event.rawX - initialTouchX).toInt()
                            val newY = initialY + (event.rawY - initialTouchY).toInt()
                            params?.x = newX
                            params?.y = newY
                            offsetX = newX
                            offsetY = newY
                            windowManager?.updateViewLayout(crosshairView, params)
                            return true
                        }
                    }
                    return false
                }
            })
        }

        try {
            windowManager?.addView(crosshairView, params)
            isRunning = true
        } catch (_: Exception) {
            isRunning = false
        }
    }

    private fun updateCrosshair() {
        if (crosshairView == null || params == null) return

        val density = resources.displayMetrics.density
        val totalSizePx = ((sizeDp + 30) * density).toInt()

        params?.width = totalSizePx
        params?.height = totalSizePx
        params?.x = offsetX
        params?.y = offsetY

        params?.flags = if (isLocked) {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        } else {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        try {
            windowManager?.updateViewLayout(crosshairView, params)
            crosshairView?.invalidate()
        } catch (_: Exception) {}
    }

    private fun stopCrosshair() {
        crosshairView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        crosshairView = null
        params = null
        isRunning = false
    }

    override fun onDestroy() {
        stopCrosshair()
        super.onDestroy()
    }

    private class CrosshairOverlayView(context: Context) : View(context) {
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val density = resources.displayMetrics.density
            val alphaInt = ((opacityPercent / 100f) * 255).toInt().coerceIn(0, 255)

            val baseColor = colorArgb
            val r = Color.red(baseColor)
            val g = Color.green(baseColor)
            val b = Color.blue(baseColor)
            val finalColor = Color.argb(alphaInt, r, g, b)

            linePaint.color = finalColor
            linePaint.strokeWidth = strokeWidthDp * density
            fillPaint.color = finalColor

            val cx = width / 2f
            val cy = height / 2f
            val gapPx = gapSizeDp * density
            val lengthPx = lineLengthDp * density

            when (crosshairStyle) {
                0 -> {
                    // Tactical Dot (Ponto Cirúrgico)
                    val dotRadius = (sizeDp / 4f) * density
                    canvas.drawCircle(cx, cy, dotRadius, fillPaint)
                    // Outer subtle ring
                    linePaint.strokeWidth = 1.5f * density
                    canvas.drawCircle(cx, cy, dotRadius + (4f * density), linePaint)
                }
                1 -> {
                    // Classic Esports Cross (4 Linhas com Espaçamento Central)
                    // Top
                    canvas.drawLine(cx, cy - gapPx, cx, cy - gapPx - lengthPx, linePaint)
                    // Bottom
                    canvas.drawLine(cx, cy + gapPx, cx, cy + gapPx + lengthPx, linePaint)
                    // Left
                    canvas.drawLine(cx - gapPx, cy, cx - gapPx - lengthPx, cy, linePaint)
                    // Right
                    canvas.drawLine(cx + gapPx, cy, cx + gapPx + lengthPx, cy, linePaint)
                }
                2 -> {
                    // Circle + Cross (Círculo Tático com Ponto Central)
                    val ringRadius = (sizeDp / 2.5f) * density
                    canvas.drawCircle(cx, cy, ringRadius, linePaint)
                    // 4 short ticks
                    canvas.drawLine(cx, cy - ringRadius - (4f * density), cx, cy - ringRadius + (2f * density), linePaint)
                    canvas.drawLine(cx, cy + ringRadius - (2f * density), cx, cy + ringRadius + (4f * density), linePaint)
                    canvas.drawLine(cx - ringRadius - (4f * density), cy, cx - ringRadius + (2f * density), cy, linePaint)
                    canvas.drawLine(cx + ringRadius - (2f * density), cy, cx + ringRadius + (4f * density), cy, linePaint)
                }
                3 -> {
                    // T-Cross (Estilo CS / AWM sem topo)
                    // Bottom
                    canvas.drawLine(cx, cy + gapPx, cx, cy + gapPx + lengthPx, linePaint)
                    // Left
                    canvas.drawLine(cx - gapPx, cy, cx - gapPx - lengthPx, cy, linePaint)
                    // Right
                    canvas.drawLine(cx + gapPx, cy, cx + gapPx + lengthPx, cy, linePaint)
                }
                4 -> {
                    // Chevron Triangles / Precision Diamond
                    val dSize = (sizeDp / 3f) * density
                    val path = android.graphics.Path().apply {
                        moveTo(cx, cy - dSize)
                        lineTo(cx + dSize, cy)
                        lineTo(cx, cy + dSize)
                        lineTo(cx - dSize, cy)
                        close()
                    }
                    canvas.drawPath(path, linePaint)
                }
            }

            // Center Dot
            if (hasCenterDot && crosshairStyle != 0) {
                canvas.drawCircle(cx, cy, centerDotRadiusDp * density, fillPaint)
            }

            // Outer Optional Ring
            if (hasOuterRing && crosshairStyle != 2) {
                val outerRadius = (sizeDp / 2f) * density
                linePaint.strokeWidth = 1.2f * density
                canvas.drawCircle(cx, cy, outerRadius, linePaint)
            }

            // Calibration box in unlock mode
            if (!isLocked) {
                linePaint.strokeWidth = 1f * density
                linePaint.color = Color.argb(160, 56, 189, 248)
                canvas.drawRect(2f, 2f, width.toFloat() - 2f, height.toFloat() - 2f, linePaint)
            }
        }
    }
}
