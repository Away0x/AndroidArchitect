package com.away0x.hi.ui.app.demo.refresh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.away0x.hi.library.log.HiLog
import com.away0x.hi.ui.app.R
import com.away0x.hi.ui.refresh.HiTextOverView
import com.away0x.hi.ui.refresh.IHiRefresh
import kotlinx.android.synthetic.main.activity_hi_refresh_demo.*

class HiRefreshDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_refresh_demo)

        initRefreshLayout()
        initRecycleView()
    }

    private fun initRefreshLayout() {
        // 设置 text over view
        // val textOverView = HiTextOverView(this)
        // refresh_layout.setRefreshOverView(textOverView)

        // 设置自定义动画 over view
        val lottieOverView = HiLottieOverView(this)
        refresh_layout.setRefreshOverView(lottieOverView)

        refresh_layout.setRefreshListener(object : IHiRefresh.HiRefreshListener {
            override fun enableRefresh(): Boolean {
                return true
            }

            override fun onRefresh() {
                Handler().postDelayed({ refresh_layout.refreshFinished() }, 1000) // 模拟网络加载
            }
        })
    }

    private fun initRecycleView() {
        val data = mutableListOf("HiRefresh", "HiRefresh", "HiRefresh", "HiRefresh", "HiRefresh", "HiRefresh", "HiRefresh")

        recycleview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recycleview.layoutManager = layoutManager
        recycleview.adapter = MyAdater(data)
    }

    class MyAdater(private val data: MutableList<String>) : RecyclerView.Adapter<MyAdater.MyViewHodler>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHodler {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return MyViewHodler(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: MyViewHodler, position: Int) {
            holder.textView.text = data[position]
            holder.itemView.setOnClickListener { HiLog.d("position: $position") }
        }

        class MyViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textView: TextView = itemView.findViewById(R.id.tv_title)
        }

    }

}