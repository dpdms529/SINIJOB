package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.Recruit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BookmarkFragment extends Fragment {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    TextView itemNum;

    AppDatabase db;

    Context context;
    SharedPreference pref;

    public BookmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recommendView);
        itemNum = view.findViewById(R.id.itemNum);

        db = AppDatabase.getInstance(this.getContext());
        pref = new SharedPreference(context);

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 구분선
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new OnRecoItemClickListener() {
            @Override
            public void OnItemClick(RecommendAdapter.ViewHolder holder, View view, int position) {
                Recommendation item = adapter.getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        loadListData();

        return view;
    }

    private void loadListData() {
        ArrayList<Recommendation> items = new ArrayList<>();

        // 북마크 테이블 읽어오기
        String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_read.php?user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "");
        URLConnector urlConnectorBookmark = new URLConnector(bookmarkPhp);
        urlConnectorBookmark.start();
        try {
            urlConnectorBookmark.join();
        } catch (InterruptedException e) {
        }
        String bookmarkResult = urlConnectorBookmark.getResult();

        try {
            JSONObject jsonObject = new JSONObject(bookmarkResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String recruit_id = jsonObject1.getString("recruit_id");
                Log.d("TAG", "loadListData: "+recruit_id);
                List<Recruit> recruit = null;
                try {
                    recruit = new Query.RecruitGetListAsyncTask(db.RecruitDao()).execute(recruit_id).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "loadListData: "+recruit);

                int flag = 0;
                String salaryType = new String();
                switch (recruit.get(0).salary_type_code) {     // 급여 타입에 알맞은 단어
                    case "H":
                        salaryType = "시";
                        break;
                    case "D":
                        salaryType = "일";
                        break;
                    case "M":
                        salaryType = "월";
                        break;
                    case "Y":
                        salaryType = "연";
                        break;
                }
                String sal = new String();
                String[] tmp = recruit.get(0).salary.split(" ~ ");
                if (tmp.length == 1 || tmp[0].equals(tmp[1])) {
                    String[] tmp2 = tmp[0].split("만원|원");
                    sal = tmp2[0];
                } else {
                    String[] tmp2 = tmp[0].split("만원|원");
                    String[] tmp3 = tmp[1].split("만원|원");
                    sal = tmp2[0] + " ~ " + tmp3[0];
                }
                DistanceCalculator distance = new DistanceCalculator(pref.preferences.getString(SharedPreference.X,"127.12934"), pref.preferences.getString(SharedPreference.Y,"35.84688"), recruit.get(0).x_coordinate, recruit.get(0).y_coordinate);
                Double dist = distance.getStraightDist();   // 직선거리 구하는 함수
                items.add(new Recommendation(recruit.get(0).recruit_id, recruit.get(0).organization, recruit.get(0).recruit_title, salaryType, sal, dist, true));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        itemNum.setText(String.valueOf(items.size()));
        adapter.setItems(items);
    }
}