package com.example.projekat3.presentation.view.activities


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projekat3.R
import com.example.projekat3.data.models.stocks.DetailedStock
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class DetailedStockActivity : AppCompatActivity() {

    lateinit var buyButton: Button
    lateinit var sellButton: Button
    lateinit var stockSymbol: TextView
    lateinit var stockValue: TextView
    lateinit var chart: LineChart
    lateinit var mktCap: TextView
    lateinit var open: TextView
    lateinit var bid: TextView
    lateinit var close: TextView
    lateinit var ask: TextView
    lateinit var divYield: TextView
    lateinit var pe: TextView
    lateinit var eps: TextView
    lateinit var ebit: TextView
    lateinit var beta: TextView

    lateinit var detailedStock: DetailedStock

    private var balance = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_stock)

        if (intent.getParcelableExtra<DetailedStock>("detailedStock") != null) {
            detailedStock = intent.getParcelableExtra("detailedStock")!!
            balance = intent.getDoubleExtra("balance", 0.0)
        } else {
            Toast.makeText(this, "Error while loading stock", Toast.LENGTH_LONG).show()
            return
        }

        initFields()
        setData()
        setListeners()
    }

    private fun initFields() {
        buyButton = findViewById<View>(R.id.buyButton) as Button
        sellButton = findViewById<View>(R.id.sellButton) as Button

        stockSymbol = findViewById<View>(R.id.stockSymbolDetails) as TextView
        stockValue = findViewById<View>(R.id.stockValueDetails) as TextView

        chart = findViewById<View>(R.id.stockDetailsChart) as LineChart

        mktCap = findViewById<View>(R.id.mktCapTextView) as TextView
        open = findViewById<View>(R.id.openTextView) as TextView
        bid = findViewById<View>(R.id.bidTextView) as TextView
        close = findViewById<View>(R.id.closeTextView) as TextView
        ask = findViewById<View>(R.id.askTextView) as TextView
        divYield = findViewById<View>(R.id.divYieldTextView) as TextView
        pe = findViewById<View>(R.id.peTextView) as TextView
        eps = findViewById<View>(R.id.epsTextView) as TextView
        ebit = findViewById<View>(R.id.ebitTextView) as TextView
        beta = findViewById<View>(R.id.betaTextView) as TextView
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        stockSymbol.text = "symbol: " + detailedStock.symbol
        stockValue.text = "value: " + detailedStock.last.toString()

        mktCap.text = "mtkCap: " + detailedStock.metrics.marketCup.toString()
        open.text = "open: " + detailedStock.open.toString()
        bid.text = "bid: " + detailedStock.bid.toString()
        close.text = "close: " + detailedStock.close.toString()
        ask.text = "ask: " + detailedStock.ask.toString()
        divYield.text = ""
        pe.text = "pe: " + (detailedStock.last / detailedStock.metrics.marketCup).toString()
        eps.text = "eps: " + detailedStock.metrics.eps.toString()
        ebit.text = "ebit: " + detailedStock.metrics.ebit.toString()
        beta.text = "beta: " + detailedStock.metrics.beta.toString()

        //chart
        val ourLineChartEntries: ArrayList<Entry> = ArrayList()
        var i = 0

        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)

        detailedStock.chart.bars.forEach {
            val value = it.price.toFloat()
            ourLineChartEntries.add(Entry(i++.toFloat(), value))
        }

        val lineDataSet = LineDataSet(ourLineChartEntries, "")
        lineDataSet.color = Color.BLACK
        val data = LineData(lineDataSet)
        chart.data = data
        chart.invalidate()
    }

    private fun setListeners() {//todo
        buyButton.setOnClickListener {
            startBuyActivity()
        }

        sellButton.setOnClickListener {
            startSellActivity()
        }
    }


    fun startSellActivity() {
        val intent = Intent(this, SellActivity::class.java)
        intent.putExtra("name", detailedStock.name)
        intent.putExtra("symbol", detailedStock.name)
        intent.putExtra("numberOfOwned", this.intent.getIntExtra("numberOfOwned", 0))
        doSellAction.launch(intent)
    }

    private val doSellAction: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data

            val numberOfSold = data?.getIntExtra("numberOfSold",0)!!
            val balanceGained = numberOfSold.times(detailedStock.last)

            val returnIntent = Intent()
            returnIntent.putExtra("numberOfBought", numberOfSold * -1)
            returnIntent.putExtra("balanceSpent", balanceGained)
            returnIntent.putExtra("name", detailedStock.name)
            returnIntent.putExtra("symbol", detailedStock.symbol)
            this.setResult(RESULT_OK, returnIntent)
            this.finish()
        }
    }


    fun startBuyActivity() {
        val intent = Intent(this, BuyActivity::class.java)
        intent.putExtra("detailedStock", detailedStock)
        intent.putExtra("balance", balance)
        intent.putExtra("last", detailedStock.last)
        doBuyAction.launch(intent)
    }

    private val doBuyAction: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data

            var numberOfBought = 0
            var balanceSpent = 0.0

            if (data?.getStringExtra("mode") == "balance") {
                val balanceEntered = data.getIntExtra("number", 0)
                numberOfBought = (balanceEntered / detailedStock.last).toInt()
                balanceSpent  = numberOfBought * detailedStock.last
            }
            else if (data?.getStringExtra("mode") == "number") {
                numberOfBought = data.getIntExtra("number", 0)
                balanceSpent = numberOfBought * detailedStock.last
            }

            val returnIntent = Intent()
            returnIntent.putExtra("numberOfBought", numberOfBought)
            returnIntent.putExtra("balanceSpent", balanceSpent * -1)
            returnIntent.putExtra("name", detailedStock.name)
            returnIntent.putExtra("symbol", detailedStock.symbol)
            this.setResult(RESULT_OK, returnIntent)
            this.finish()
        }
    }

}