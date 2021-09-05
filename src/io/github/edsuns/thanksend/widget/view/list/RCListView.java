package io.github.edsuns.thanksend.widget.view.list;

import io.github.edsuns.thanksend.ui.Colors;
import io.github.edsuns.thanksend.widget.layout.ScrollUI;
import io.github.edsuns.thanksend.widget.layout.VerticalFlowLayout;
import io.github.edsuns.thanksend.widget.message.MessageAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class RCListView extends JScrollPane {
    private BaseAdapter<ViewHolder> adapter;
    private JPanel contentPanel;
    private final int vGap;
    private final int hGap;
    boolean scrollToBottom = false;
    private ScrollUI scrollUI;

    // 监听滚动到顶部事件
    private ScrollListener scrollListener;
    private boolean scrollBarPressed = false;
    private int lastScrollValue = -1;

    private static int lastItemCount = 0;
    private MouseAdapter scrollMouseListener;
    private boolean messageLoading = false;
    private long lastWeelTime = 0;

    public RCListView() {
        this(0, 0);
    }

    public RCListView(int hGap, int vGap) {
        this.vGap = vGap;
        this.hGap = hGap;

        initComponents();
        setListeners();
    }

    public void setScrollHiddenOnMouseLeave(JComponent component) {
        if (scrollMouseListener == null) {

            scrollMouseListener = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
                    getVerticalScrollBar().repaint();

                    super.mouseEntered(e);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setScrollBarColor(Colors.WINDOW_BACKGROUND, Colors.WINDOW_BACKGROUND);
                    getVerticalScrollBar().repaint();

                    super.mouseExited(e);
                }
            };
        }

        component.addMouseListener(scrollMouseListener);
    }

    /**
     * 设置滚动条的颜色，此方法必须在setAdapter()方法之前执行
     */
    public void setScrollBarColor(Color thumbColor, Color trackColor) {
        if (scrollUI == null) {
            scrollUI = new ScrollUI(thumbColor, trackColor);
            getVerticalScrollBar().setUI(scrollUI);
        } else {
            scrollUI.setThumbColor(thumbColor);
            scrollUI.setTrackColor(trackColor);
        }
    }

    private void initComponents() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, hGap, vGap, true, false));
        contentPanel.setBackground(Colors.WINDOW_BACKGROUND);

        setViewportView(contentPanel);
        setBorder(BorderFactory.createEmptyBorder());
        getVerticalScrollBar().setUnitIncrement(25);
        setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
    }

    private void setListeners() {
        // 之所以要加上!scrollBarPressed这个条件，scrollBar在顶部的时间，scrollbar点击和释放都分别会触发adjustmentValueChanged这个事件
        // 所以只让scrollBar释放的时候触发这个回调
        // !scrollToBottom 这个条件保证在自动滚动到底部之前，不会调用此回调
        AdjustmentListener adjustmentListener = evt -> {
            int val = evt.getValue();
            int max = getVerticalScrollBar().getMaximum();
            int extent = getVerticalScrollBar().getModel().getExtent();

            // 之所以要加上!scrollBarPressed这个条件，scrollBar在顶部的时间，scrollbar点击和释放都分别会触发adjustmentValueChanged这个事件
            // 所以只让scrollBar释放的时候触发这个回调
            // !scrollToBottom 这个条件保证在自动滚动到底部之前，不会调用此回调
            if (scrollListener != null) {
                if (evt.getValue() == 0 && evt.getValue() != lastScrollValue && !scrollBarPressed && !scrollToBottom) {
                    messageLoading = true;
                    scrollListener.onScrollToTop();
                } else if ((max - (val + extent)) < 1) {
                    scrollListener.onScrollToBottom();
                }
            }


            if (evt.getAdjustmentType() == AdjustmentEvent.TRACK && scrollToBottom) {
                getVerticalScrollBar().setValue(getVerticalScrollBar().getModel().getMaximum()
                        - getVerticalScrollBar().getModel().getExtent());
            }

            lastScrollValue = evt.getValue();

        };

        // 如果两次鼠标滚轮间隔小于1秒，则忽略
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scrollToBottom = false;
                scrollBarPressed = true;
                super.mouseEntered(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                scrollBarPressed = false;
                super.mouseReleased(e);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // 如果两次鼠标滚轮间隔小于1秒，则忽略
                if (System.currentTimeMillis() - lastWeelTime < 1000) {
                    lastWeelTime = System.currentTimeMillis();
                    return;
                }

                if (getVerticalScrollBar().getValue() == 0) {
                    if (messageLoading) {
                        messageLoading = false;
                    } else {
                        if (scrollListener != null) {
                            scrollListener.onScrollToTop();
                        }
                    }

                }

                scrollToBottom = false;

                lastWeelTime = System.currentTimeMillis();

                super.mouseWheelMoved(e);
            }
        };

        getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        getVerticalScrollBar().addMouseListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        // 滚动条自动隐藏
        setScrollHiddenOnMouseLeave(this);
        setScrollHiddenOnMouseLeave(getVerticalScrollBar());
    }

    public void setAutoScrollToBottom() {
        scrollToBottom = true;
    }

    public void setAutoScrollToTop() {
        scrollToBottom = false;
        getVerticalScrollBar().setValue(1);
    }

    public void fillComponents() {
        if (adapter == null) {
            return;
        }

        lastItemCount = adapter.getCount();

        for (int i = 0; i < adapter.getCount(); i++) {
            int viewType = adapter.getItemViewType(i);
            ViewHolder viewHolder = adapter.onCreateHeaderViewHolder(viewType, i);
            if (viewHolder != null) {
                adapter.onBindHeaderViewHolder(viewHolder, i);
                contentPanel.add(viewHolder);
            }

            ViewHolder holder = adapter.onCreateViewHolder(viewType);
            adapter.onBindViewHolder(holder, i);
            contentPanel.add(holder);
        }
    }

    public BaseAdapter<ViewHolder> getAdapter() {
        return adapter;
    }

    public void setAdapter(MessageAdapter adapter) {
        this.adapter = adapter;

        fillComponents();
    }

    public void setContentPanelBackground(Color color) {
        contentPanel.setOpaque(true);
        contentPanel.setBackground(color);
    }

    public void scrollToBottom() {
        setAutoScrollToBottom();
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
    }

//    /**
//     * 获取滚动条在底部时显示的条目数
//     */
//    private int getLastVisibleItemCount() {
//        int height = getHeight();
//
//        int elemHeight = 0;
//        int count = 0;
//        for (int i = contentPanel.getComponentCount() - 1; i >= 0; i--) {
//            count++;
//            int h = contentPanel.getComponent(i).getHeight() + 20;
//            elemHeight += h;
//
//            if (elemHeight >= height) {
//                break;
//            }
//        }
//
//        return count;
//    }


    /**
     * 重绘整个listView
     */
    public void notifyDataSetChanged(boolean keepSize) {
        if (keepSize) {
            if (lastItemCount == adapter.getCount()) {
                // 保持原来内容面板的宽高，避免滚动条长度改变或可见状态改变时闪屏
                contentPanel.setPreferredSize(new Dimension(contentPanel.getWidth(), contentPanel.getHeight()));
            }
        }

        contentPanel.removeAll();

        fillComponents();

        contentPanel.revalidate();
        contentPanel.repaint();

    }

    /**
     * 重绘指定区间内的元素
     */
    public void notifyItemRangeInserted(int startPosition, int count) {
        for (int i = count - 1; i >= startPosition; i--) {
            int viewType = adapter.getItemViewType(i);
            ViewHolder holder = adapter.onCreateViewHolder(viewType);
            adapter.onBindViewHolder(holder, i);
            contentPanel.add(holder, startPosition);
        }
    }

    /**
     * 重绘指定位置的元素
     */
    public void notifyItemChanged(int position) {
        ViewHolder holder = (ViewHolder) getItem(position);
        adapter.onBindViewHolder(holder, position);
        holder.revalidate();
        holder.repaint();
    }

    public Component getItem(int n) {
        return contentPanel.getComponent(n);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setScrollListener(ScrollListener listener) {
        this.scrollListener = listener;
    }

    public void notifyItemInserted(int position, boolean end) {
        int viewType = adapter.getItemViewType(position);
        ViewHolder holder = adapter.onCreateViewHolder(viewType);
        adapter.onBindViewHolder(holder, position);

        position = end ? -1 : position;
        contentPanel.add(holder, position);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void notifyItemRemoved(int position) {
        contentPanel.remove(position);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * 获取列表中所有的ViewHolder项目，不包括HeaderViewHolder
     */
    public List<Component> getItems() {
        Component[] components = contentPanel.getComponents();
        List<Component> viewHolders = new ArrayList<>();
        for (Component com : components) {
            if (!(com instanceof ViewHolder)) {
                viewHolders.add(com);
            }
        }

        return viewHolders;
    }

    public interface ScrollListener {
        void onScrollToTop();

        void onScrollToBottom();
    }
}
