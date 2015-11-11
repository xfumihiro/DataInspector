package data_inspector.ui.dialog.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import data_inspector.R;
import java.io.File;
import rx.functions.Action1;

public class StorageListAdapter extends RecyclerView.Adapter<StorageListAdapter.ViewHolder>
    implements Action1<String> {
  private final Context context;
  private final LayoutInflater layoutInflater;
  private File[] files;

  public StorageListAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public void call(String folder) {
    File filesDir = new File("/data/data/" + context.getPackageName() + "/" + folder);
    files = filesDir.listFiles();
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        layoutInflater.inflate(R.layout.data_inspector_storage_usage_listitem, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("SetTextI18n") @Override
  public void onBindViewHolder(StorageListAdapter.ViewHolder holder, int position) {
    holder.file.setText(files[position].getName());
    holder.size.setText(Long.toString(files[position].length()));
  }

  @Override public int getItemCount() {
    return files != null ? files.length : 0;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView file;
    TextView size;

    public ViewHolder(View itemView) {
      super(itemView);
      file = (TextView) itemView.findViewById(R.id.file);
      size = (TextView) itemView.findViewById(R.id.size);
    }
  }
}
