package com.ilkeruzer.basketballheatmap.view

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textview.MaterialTextView
import com.ilkeruzer.basketballheatmap.R
import com.ilkeruzer.basketballheatmap.model.XYValue
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import com.murgupluoglu.request.STATUS_ERROR
import com.murgupluoglu.request.STATUS_LOADING
import com.murgupluoglu.request.STATUS_SUCCESS
import com.timqi.sectorprogressview.ColorfulRingProgressView
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModel()

    private lateinit var mScatterPlot: GraphView
    var xyValueArray: ArrayList<XYValue>? = null
    private var userId = MutableLiveData(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initGraphic()
        initApiObserve()
        getAllFilterData()
        buttonClicked()
        rateInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun rateInfo() {
        viewModel.getRateInfo().observe(this, {
            Log.d(TAG, "rateInfo: $it")
            findViewById<ColorfulRingProgressView>(R.id.sectorProgress).percent = it.successRate.toFloat()
            findViewById<MaterialTextView>(R.id.rateText).text = "%" + it.successRate.toString()
            findViewById<MaterialTextView>(R.id.totalShot).text = "Total Shot: " + it.count
            findViewById<MaterialTextView>(R.id.successShot).text = "Success Shot: " + it.success.toString()
            findViewById<MaterialTextView>(R.id.failShot).text = "Fail Shot: " + it.fail.toString()

        })
    }
    private fun buttonClicked() {
        findViewById<Button>(R.id.changeUser).setOnClickListener {
            mScatterPlot.removeAllSeries()
            if (userId.value == 1) {
                userId.postValue(0)
            } else {
                userId.postValue(1)
            }
        }
    }




    private fun getAllFilterData() {
        xyValueArray = ArrayList()
        viewModel.getFilterShot().observe(this, { list ->
            Log.d(TAG, "getAllFilterData: size ${list.size}" )
            list.forEach {
                Log.d(TAG, "getAllFilterData: $it")
                val x = it.shotPosX - 0.3
                val y = it.shotPosY + 4.9
                val series1 = PointsGraphSeries<DataPoint>()
                series1.shape = PointsGraphSeries.Shape.POINT

                when {
                    it.say <= 5 -> series1.size = 16f
                    it.say in 6..10 -> series1.size = 24f
                    it.say in 11..20 -> series1.size =  32f
                    it.say < 20 -> series1.size = 42f
                }

                if (it.success > 0.0 && it.success < 12.5) {
                    series1.color = Color.rgb(178, 24, 43)
                    series1.appendData(DataPoint(x, y), false, 1000)
                } else if (it.success > 12.5 && it.success < 25.0) {
                    series1.color = Color.rgb(244, 109, 67)
                    series1.appendData(DataPoint(x, y), false, 1000)
                   // mScatterPlot.addSeries(series1)
                } else if (it.success > 25.0 && it.success < 37.5) {
                    series1.color = Color.rgb(253, 174, 97)
                    series1.appendData(DataPoint(x, y), false, 1000)
                } else if (it.success > 37.5 && it.success < 50.0) {
                    series1.color = Color.rgb(254, 224, 139)
                    series1.appendData(DataPoint(x, y), false, 1000)

                } else if (it.success >= 50.0 && it.success < 62.5) {
                    series1.color = Color.rgb(230, 245, 152)
                    series1.appendData(DataPoint(x, y), false, 1000)

                } else if (it.success > 62.5 && it.success < 75.0) {
                    series1.color = Color.rgb(171, 221, 164)
                    series1.appendData(DataPoint(x, y), false, 1000)
                } else if (it.success > 75.0 && it.success < 87.5) {
                    series1.color = Color.rgb(102, 194, 165)
                    series1.appendData(DataPoint(x, y), false, 1000)

                } else if (it.success > 87.5 && it.success <= 100) {
                    series1.color = Color.rgb(50, 136, 189)
                    series1.appendData(DataPoint(x, y), false, 1000)

                }

                mScatterPlot.addSeries(series1)
            }
            createScatterPlot()
        })

    }

    private fun createScatterPlot() {
        Log.d(TAG, "createScatterPlot: Creating scatter plot.")
        //set Scrollable and Scaleable
        mScatterPlot.viewport.isScalable = false
        mScatterPlot.viewport.setScalableY(false)
        mScatterPlot.viewport.isScrollable = false
        mScatterPlot.viewport.setScrollableY(false)

        //set manual x bounds
        mScatterPlot.viewport.isYAxisBoundsManual = true
        mScatterPlot.viewport.setMaxY(7.5)
        mScatterPlot.viewport.setMinY(-7.5)

        //set manual y bounds
        mScatterPlot.viewport.isXAxisBoundsManual = true
        mScatterPlot.viewport.setMaxX(7.5)
        mScatterPlot.viewport.setMinX(-7.5)
        //mScatterPlot.addSeries(xySeries)


    }

    private fun initGraphic() {
        mScatterPlot = findViewById(R.id.graph)
        mScatterPlot.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        mScatterPlot.gridLabelRenderer.verticalLabelsColor = Color.TRANSPARENT
        mScatterPlot.gridLabelRenderer.horizontalLabelsColor = Color.TRANSPARENT
    }

    private fun initApiObserve() {
        viewModel.dataResponse.observe(this, {
            when (it.status) {
                STATUS_LOADING -> {
                }
                STATUS_SUCCESS -> {
                    Log.d(TAG, "onCreate: service is success")
                    it.responseObject?.let { data ->
                        userId.observe(this, { userId ->
                            viewModel.shotInsertDb(data.data[userId].shots)
                        })

                        //initPoints(data.data[0].shots)

                    }
                }
                STATUS_ERROR -> {
                    Log.e(TAG, "onCreate: error service " + it.errorMessage)
                }
            }
        })
    }
}