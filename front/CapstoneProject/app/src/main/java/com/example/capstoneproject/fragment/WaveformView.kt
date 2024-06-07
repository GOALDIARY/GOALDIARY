package com.example.capstoneproject.fragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class WaveformView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var amplitude = 0f // 전체 진폭
    private val numBars = 5 // 바의 개수
    private val phaseShift = 2.0 * Math.PI / numBars // 각 막대의 위상 차이

    fun updateAmplitude(amp: Float) {
        amplitude = amp
        invalidate() // 뷰 갱신 요청
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val barWidth = width.toFloat() / (numBars + (numBars * 2)) // 간격 줄임
        val centerY = height / 2
        val maxAmplitude = centerY * 0.6f // 진폭 최대치 조정
        val totalBarWidth = numBars * barWidth * 1.5f // 모든 막대의 총 너비
        val startOffset = (width - totalBarWidth) / 2 // 시작점을 중앙 정렬하기 위한 오프셋

        val paint = Paint().apply {
            strokeWidth = barWidth / 2 // 막대 두께
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND // 막대 끝을 둥글게
        }

        val time = System.currentTimeMillis() / 1000.0 // 시간 변수 (애니메이션 효과)
        for (i in 0 until numBars) {
            paint.color = if (i % 2 == 0) Color.parseColor("#FFA07A") else Color.parseColor("#3CB371") // 색상 변경
            val phase = i * phaseShift
            val x = (startOffset + i * (barWidth * 1.5) + barWidth / 2).toFloat() // 각 막대의 시작점, 중앙 정렬을 위한 오프셋 추가, Float로 변환
            val barHeight = (sin(time + phase) * amplitude * maxAmplitude).toFloat() // 시간과 위상을 이용한 물결 계산, Float로 변환
            canvas.drawLine(x, centerY - barHeight, x, centerY + barHeight, paint)
        }
    }

}
