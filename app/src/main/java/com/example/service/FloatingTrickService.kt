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

class FloatingTrickService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: TrickOverlayView? = null
    private var params: WindowManager.LayoutParams? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE_CONFIG = "ACTION_UPDATE_CONFIG"

        var isRunning = false
            private set

        // Config state
        var shapeType = 0 // 0: Ring Anchor, 1: Crosshair, 2: Dot, 3: Double Ring Anti-Overaim
        var overlaySize = 56 // dp
        var overlayOpacity = 85 // 0-100%
        var overlayColor = 0xFFE53935.toInt() // Red Primary
        var strokeWidthDp = 3f
        var isLocked = false // if true, touches pass through to game
        var showAntiOveraimBar = true // draws horizontal boundary line
        var boundaryDistanceDp = 70 // distance above button for boundary
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopFloatingOverlay()
                stopSelf()
            }
            ACTION_UPDATE_CONFIG -> {
                updateOverlay()
            }
            ACTION_START, null -> {
                startFloatingOverlay()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startFloatingOverlay() {
        if (floatingView != null) {
            updateOverlay()
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
        val totalSizePx = ((overlaySize + (if (showAntiOveraimBar) boundaryDistanceDp + 20 else 20)) * density).toInt()

        params = WindowManager.LayoutParams(
            totalSizePx,
            totalSizePx,
            layoutType,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = 100
            y = 100
        }

        floatingView = TrickOverlayView(this).apply {
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
                            params?.x = initialX + (event.rawX - initialTouchX).toInt()
                            params?.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager?.updateViewLayout(floatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
        }

        try {
            windowManager?.addView(floatingView, params)
            isRunning = true
        } catch (_: Exception) {
            isRunning = false
        }
    }

    private fun updateOverlay() {
        if (floatingView == null || params == null) return

        val density = resources.displayMetrics.density
        val totalSizePx = ((overlaySize + (if (showAntiOveraimBar) boundaryDistanceDp + 20 else 20)) * density).toInt()

        params?.width = totalSizePx
        params?.height = totalSizePx

        params?.flags = if (isLocked) {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        } else {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        try {
            windowManager?.updateViewLayout(floatingView, params)
            floatingView?.invalidate()
        } catch (_: Exception) {}
    }

    private fun stopFloatingOverlay() {
        floatingView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        floatingView = null
        params = null
        isRunning = false
    }

    override fun onDestroy() {
        stopFloatingOverlay()
        super.onDestroy()
    }

    // Custom Canvas renderer for high-performance HUD overlay
    private class TrickOverlayView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val density = resources.displayMetrics.density
            val alphaInt = ((overlayOpacity / 100f) * 255).toInt().coerceIn(0, 255)

            val baseColor = overlayColor
            val red = Color.red(baseColor)
            val green = Color.green(baseColor)
            val blue = Color.blue(baseColor)
            val computedColor = Color.argb(alphaInt, red, green, blue)

            val strokePx = strokeWidthDp * density
            paint.strokeWidth = strokePx
            paint.color = computedColor

            val cx = width / 2f
            // If boundary bar is active, place button in lower area so boundary fits above
            val cy = if (showAntiOveraimBar) height * 0.7f else height / 2f
            val radiusPx = (overlaySize / 2f) * density

            when (shapeType) {
                0 -> {
                    // Ring Anchor (Anel de ancoragem)
                    canvas.drawCircle(cx, cy, radiusPx, paint)
                    fillPaint.color = Color.argb(alphaInt / 3, red, green, blue)
                    canvas.drawCircle(cx, cy, radiusPx, fillPaint)
                    // Center micro dot
                    fillPaint.color = computedColor
                    canvas.drawCircle(cx, cy, 3f * density, fillPaint)
                }
                1 -> {
                    // Tactical Crosshair (Cruz de Precisão)
                    canvas.drawCircle(cx, cy, radiusPx, paint)
                    val lineLen = radiusPx * 0.5f
                    canvas.drawLine(cx - lineLen, cy, cx + lineLen, cy, paint)
                    canvas.drawLine(cx, cy - lineLen, cx, cy + lineLen, paint)
                    fillPaint.color = computedColor
                    canvas.drawCircle(cx, cy, 2.5f * density, fillPaint)
                }
                2 -> {
                    // Reaper Surgical Dot (Ponto Cirúrgico)
                    fillPaint.color = computedColor
                    canvas.drawCircle(cx, cy, 6f * density, fillPaint)
                    canvas.drawCircle(cx, cy, 14f * density, paint)
                }
                3 -> {
                    // Double Ring Pro Anti-Overaim
                    canvas.drawCircle(cx, cy, radiusPx, paint)
                    canvas.drawCircle(cx, cy, radiusPx * 0.6f, paint)
                    fillPaint.color = computedColor
                    canvas.drawCircle(cx, cy, 3f * density, fillPaint)
                }
            }

            // Draw Anti-Overaim Boundary Line (Barra Anti-Passar da Cabeça)
            if (showAntiOveraimBar) {
                val boundaryY = cy - (boundaryDistanceDp * density)
                paint.strokeWidth = 2.5f * density
                paint.color = Color.argb((alphaInt * 0.85f).toInt(), 255, 60, 60)

                val barHalfWidth = radiusPx * 1.4f
                canvas.drawLine(cx - barHalfWidth, boundaryY, cx + barHalfWidth, boundaryY, paint)

                // Downward target ticks
                canvas.drawLine(cx - barHalfWidth, boundaryY, cx - barHalfWidth, boundaryY + (6f * density), paint)
                canvas.drawLine(cx + barHalfWidth, boundaryY, cx + barHalfWidth, boundaryY + (6f * density), paint)

                textPaint.color = Color.argb(alphaInt, 255, 100, 100)
                textPaint.textSize = 9f * density
                canvas.drawText("LIMITE CAPA", cx, boundaryY - (4f * density), textPaint)
            }

            // Edit mode indicator badge if not locked
            if (!isLocked) {
                paint.strokeWidth = 1f * density
                paint.color = Color.argb(180, 56, 189, 248)
                canvas.drawRect(2f, 2f, width.toFloat() - 2f, height.toFloat() - 2f, paint)
            }
        }
    }
}
