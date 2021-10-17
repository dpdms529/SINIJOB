package org.techtown.hanieum;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> implements
        OnRecoItemClickListener {
    static ArrayList<Recommendation> items = new ArrayList<Recommendation>();
    OnRecoItemClickListener listener;
    Context context;
    SharedPreference pref;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recommendation_item, viewGroup, false);
        context = viewGroup.getContext();
        pref = new SharedPreference(context);

        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Recommendation item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Recommendation item) {
        items.add(item);
    }

    public void setItems(ArrayList<Recommendation> items) {
        this.items = items;
    }

    public Recommendation getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Recommendation item) {
        items.set(position, item);
    }

    @Override
    public void OnItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.OnItemClick(holder, view, position);
        }
    }

    public void setItemClickListener(OnRecoItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName;
        TextView title;
        TextView salaryType;
        TextView salary;
        TextView won;
        TextView distance;
        TextView measure;
        ImageButton bookmark;

        public ViewHolder(View view, OnRecoItemClickListener listener) {
            super(view);

            companyName = view.findViewById(R.id.companyName);
            title = view.findViewById(R.id.advertisementTitle);
            salaryType = view.findViewById(R.id.salaryType);
            salary = view.findViewById(R.id.salary);
            won = view.findViewById(R.id.won);
            distance = view.findViewById(R.id.distance);
            measure = view.findViewById(R.id.measure);
            bookmark = view.findViewById(R.id.bookmark);

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Recommendation item = items.get(position);
                    if (item.getBookmark()) {   // 북마크 안 됨
                        item.setBookmark(false);
                        bookmark.setImageResource(R.drawable.star);

                        String uId = pref.preferences.getString(SharedPreference.USER_ID, "");   // 유저 아이디
                        String rId = item.getId();
                        String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_del.php?user_id=" + uId + "&recruit_id=" + rId;
                        URLConnector urlConnectorBookmark = new URLConnector(bookmarkPhp);
                        urlConnectorBookmark.start();
                        try {
                            urlConnectorBookmark.join();
                        } catch (InterruptedException e) {
                        }
                    } else {    // 북마크 됨
                        item.setBookmark(true);
                        bookmark.setImageResource(R.drawable.bookmark);

                        String uId = pref.preferences.getString(SharedPreference.USER_ID, "");   // 유저 아이디
                        String rId = item.getId();
                        String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_save.php?user_id=" + uId + "&recruit_id=" + rId;
                        URLConnector urlConnectorBookmark = new URLConnector(bookmarkPhp);
                        urlConnectorBookmark.start();
                        try {
                            urlConnectorBookmark.join();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(Recommendation item) {
            String companyNm = item.getCompanyName();
            String titleNm = item.getTitle();

            for(int i=0;i<3;i++) {
                if(!titleNm.contains("/&^[a-zA-z]+/;")) {
                    titleNm=StringEscapeUtils.unescapeHtml4(titleNm);
                }
            }
            for(int i=0;i<3;i++) {
                if(!companyNm.contains("/&^[a-zA-z]+/;")) {
                    companyNm=StringEscapeUtils.unescapeHtml4(companyNm);
                }
            }
            companyName.setText(companyNm);
            title.setText(titleNm);
            String salType = item.getSalaryType();
            salaryType.setText(salType);
            salary.setText(item.getSalary());
            String wonType = new String();
            if (salType.equals("시") || salType.equals("일")) {
                wonType = "원";
            } else {
                wonType = "만원";
            }
            won.setText(wonType);
            Double dist = item.getDistance();
            if (dist >= 1000) {
                dist = dist / 1000;
                distance.setText(String.format("%.1f", dist));
                measure.setText("km");
            } else {
                distance.setText(String.format("%.0f", dist));
                measure.setText("m");
            }
            if (item.getBookmark()) {
                bookmark.setImageResource(R.drawable.bookmark);
            } else {
                bookmark.setImageResource(R.drawable.star);
            }
        }
    }

}
