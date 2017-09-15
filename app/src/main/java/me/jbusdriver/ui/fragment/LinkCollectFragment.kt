package me.jbusdriver.ui.fragment

import android.graphics.Paint
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import jbusdriver.me.jbusdriver.R
import kotlinx.android.synthetic.main.layout_recycle.*
import kotlinx.android.synthetic.main.layout_swipe_recycle.*
import me.jbusdriver.common.AppBaseRecycleFragment
import me.jbusdriver.common.KLog
import me.jbusdriver.common.dpToPx
import me.jbusdriver.mvp.LinkCollectContract
import me.jbusdriver.mvp.bean.ILink
import me.jbusdriver.mvp.bean.des
import me.jbusdriver.mvp.presenter.LinkCollectPresenterImpl
import me.jbusdriver.ui.activity.MovieListActivity
import me.jbusdriver.ui.data.collect.LinkCollector

/**
 * Created by Administrator on 2017/7/17 0017.
 */
class LinkCollectFragment : AppBaseRecycleFragment<LinkCollectContract.LinkCollectPresenter, LinkCollectContract.LinkCollectView, ILink>(), LinkCollectContract.LinkCollectView {


    override fun createPresenter() = LinkCollectPresenterImpl()

    override val swipeView: SwipeRefreshLayout? by lazy { sr_refresh }
    override val recycleView: RecyclerView by lazy { rv_recycle }
    override val layoutManager: RecyclerView.LayoutManager by lazy { LinearLayoutManager(viewContext) }


    override val adapter: BaseQuickAdapter<ILink, in BaseViewHolder> by lazy {
        object : BaseQuickAdapter<ILink, BaseViewHolder>(R.layout.layout_header_item) {
            private val actionMap by lazy {
                mapOf("收藏" to { link: ILink ->
                    LinkCollector.addToCollect(link)
                    KLog.d("link data ${LinkCollector.dataList}")
                }, "取消收藏" to { link: ILink ->
                    LinkCollector.removeCollect(link)
                    KLog.d("link data ${LinkCollector.dataList}")
                })
            }


            override fun convert(holder: BaseViewHolder, item: ILink) {
                val des = item.des.split(" ")
                KLog.d("des ${des.joinToString(",")}")
                holder.getView<TextView>(R.id.tv_head_value)?.apply {
                    setTextColor(ResourcesCompat.getColor(this@apply.resources, R.color.colorPrimaryDark, null))
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    setOnClickListener {
                        KLog.d("setOnClickListener text : $item")
                        MovieListActivity.start(it.context, item)
                    }

                    //长按操作
                    setOnLongClickListener {
                        KLog.d("setOnLongClickListener text : $item")
                        val action = if (LinkCollector.has(item)) actionMap.minus("收藏")
                        else actionMap.minus("取消收藏")

                        MaterialDialog.Builder(holder.itemView.context).content(item.des)
                                .items(action.keys)
                                .itemsCallback { _, _, _, text ->
                                    action[text]?.invoke(item)
                                }.show()
                        return@setOnLongClickListener true

                    }
                }
                val dp8 = mContext.dpToPx(8f)
                holder.itemView.setPadding(dp8 * 2, dp8, dp8 * 2, dp8)
                holder.setText(R.id.tv_head_name, des.firstOrNull())
                        .setText(R.id.tv_head_value, des.lastOrNull())
            }
        }
    }

    override val layoutId: Int = R.layout.layout_swipe_recycle

    companion object {
        fun newInstance() = LinkCollectFragment()
    }
}