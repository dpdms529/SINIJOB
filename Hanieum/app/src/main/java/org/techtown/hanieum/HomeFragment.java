package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.Recruit;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    Button summaryButton; // 음성 요약 버튼
    ImageButton searchButton; // 검색 버튼
    TextView itemNum;
    String msg; // 음성 요약 메세지

    AppDatabase db;

    Context context;

    private TextToSpeechClient ttsClient;
    TextToSpeech tts;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // kakao
        TextToSpeechManager.getInstance().initializeLibrary(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        summaryButton = view.findViewById(R.id.voice_summary_rec);
        searchButton = view.findViewById(R.id.searchButton);
        itemNum = view.findViewById(R.id.itemNum);
        msg = "추천된 일자리가 없습니다.";

        db = AppDatabase.getInstance(this.getContext());

        // 음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

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

        searchButton.setOnClickListener(this);
        summaryButton.setOnClickListener(this);

        loadListData();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            Toast.makeText(getContext(), "검색", Toast.LENGTH_SHORT).show();
        } else if (v == summaryButton) {
            ttsClient = new TextToSpeechClient.Builder()
                    .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)
                    .setSpeechSpeed(1.0)
                    .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)
                    .setListener(ttsListener)
                    .build();
            ttsClient.setSpeechText(msg);
            ttsClient.play();
        //    voiceOut(msg);
        }
    }

    private void loadListData() {
        ArrayList<Recommendation> items = new ArrayList<>();
        String _uId = "3";  // 유저 아이디
        String recoPhp = getResources().getString(R.string.serverIP)+"reco_list.php?user_id=" + _uId;
        URLConnector urlConnector = new URLConnector(recoPhp);

        HashMap<String, Integer> summary = new HashMap<String, Integer>(){{}};
        String firstDist = "0";
        String firstCorp = "0";
        String firstJob = "";

        // 북마크 테이블 읽어오기
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        String bookmarkPhp = context.getResources().getString(R.string.serverIP)+"bookmark_read.php";
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

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap<>();
                String user_id = jsonObject1.getString("user_id");
                String recruit_id = jsonObject1.getString("recruit_id");

                hashMap.put("user_id", user_id);
                hashMap.put("recruit_id", recruit_id);

                arrayList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            itemNum.setText(jsonObject.getString("rownum"));

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String recruit_id = jsonObject1.getString("recruit_id");
                List<Recruit> recruit = db.RecruitDao().getList(recruit_id);
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
                DistanceCalculator distance =  new DistanceCalculator("127.12934", "35.84688", recruit.get(0).x_coordinate, recruit.get(0).y_coordinate);
                Double dist = distance.getStraightDist();   // 직선거리 구하는 함수

                // 북마크 확인하는 코드
                for (int j=0; j<arrayList.size(); j++) {
                    HashMap<String ,String> hashMap = arrayList.get(j);
                    String uId = hashMap.get("user_id");
                    String rId = hashMap.get("recruit_id");

                    // 유저 아이디 = 3
                    if (uId.equals("3") && rId.equals(recruit.get(0).recruit_id)) {
                        flag = 1;
                    }
                }

                if (flag == 1) {    // 북마크가 되어 있을 때
                    items.add(new Recommendation(recruit.get(0).recruit_id, recruit.get(0).organization, recruit.get(0).recruit_title, salaryType, sal, dist, true));
                } else {    // 북마크가 안 되어 있을 때
                    items.add(new Recommendation(recruit.get(0).recruit_id, recruit.get(0).organization, recruit.get(0).recruit_title, salaryType, sal, dist, false));
                }

                String jobNm = db.jobCategoryDao().getCategoryName(recruit.get(0).job_code);

                if(i == 0) { // 가장 먼저 추천된 공고일 때
                    firstDist = dist.toString();
                    firstCorp = recruit.get(0).organization;
                    firstJob = jobNm;
                }

                if(summary.containsKey(jobNm)) {
                    Integer n = summary.get(jobNm);
                    n += 1;
                    summary.put(jobNm, n);
                } else {
                    summary.put(jobNm, 1);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(Integer.parseInt(itemNum.getText().toString()) > 0) {
            List<Map.Entry<String, Integer>> entryList = new LinkedList<>(summary.entrySet());

            Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                    return obj2.getValue().compareTo(obj1.getValue());
                }
            });
            msg = "추천된 공고는 총" + itemNum.getText().toString() + ", 건 입니다." +
                    "첫 번째로 추천된 공고는 " + firstCorp + "에서 모집하는, " + firstJob + ", 공고입니다." + "근무지는 현재 위치에서 " + firstDist + "km 떨어져 있습니다." +
                    "가장 많이 추천된 공고는 " + entryList.get(0).getKey() + ", " + entryList.get(0).getValue() + "건 입니다.";
        } else {

        }

        adapter.setItems(items);
    }

    // 음성 메세지 출력용
    private void voiceOut(String msg){
        if (msg.length() < 1) return;

        // 음성 출력
        tts.setPitch(0.8f); //목소리 톤1.0
        tts.setSpeechRate(0.9f);    //목소리 속도
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH,null, null);
    }

    public void onDestroy() {
        super.onDestroy();
        TextToSpeechManager.getInstance().finalizeLibrary();
    }

    private TextToSpeechListener ttsListener = new TextToSpeechListener() {
        @Override
        public void onFinished() {

        }

        @Override
        public void onError(int code, String message) {

        }
    };
}