package data_inspector.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class StockPriceAdapter extends BaseAdapter {
  private final LayoutInflater layoutInflater;
  private final List<Integer> idList;
  private final List<String> nameList;
  private final List<Float> askList;
  private final List<Float> bidList;

  public StockPriceAdapter(Context context, List<Integer> idList, List<String> nameList, List<Float> askList,
      List<Float> bidList) {
    layoutInflater = LayoutInflater.from(context);
    this.idList = idList;
    this.nameList = nameList;
    this.askList = askList;
    this.bidList = bidList;
  }

  @Override public int getCount() {
    return nameList.size();
  }

  @Override public Object getItem(int position) {
    return null;
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.listitem, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.id = (TextView) convertView.findViewById(R.id.row_id);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.ask = (TextView) convertView.findViewById(R.id.ask);
      viewHolder.bid = (TextView) convertView.findViewById(R.id.bid);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.id.setText(Integer.toString(idList.get(position)));
    viewHolder.name.setText(nameList.get(position));
    viewHolder.ask.setText(Float.toString(askList.get(position)));
    viewHolder.bid.setText(Float.toString(bidList.get(position)));
    return convertView;
  }

  class ViewHolder {
    TextView id;
    TextView name;
    TextView ask;
    TextView bid;
  }
}
