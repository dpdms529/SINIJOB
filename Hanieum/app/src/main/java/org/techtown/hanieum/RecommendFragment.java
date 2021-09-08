package org.techtown.hanieum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecommendFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    ImageButton searchButton; // 검색 버튼
    ImageButton micButton; // 마이크 버튼
    ImageButton helpButton; // 도움말 버튼
    TextView itemNum;
    TextView title; //화면 제목
    EditText editSearch;  //검색창

    ArrayList<Recommendation> items; //리사이클러뷰에서 보여줄 공고 아이템을 저장하고 있는 ArrayList
    ArrayList<Recommendation> allItems = new ArrayList<>(); //전체 공고 아이템을 저장하고있는 ArrayList

    InputMethodManager imm;
    AppDatabase db;

    SharedPreference pref;

    Context context;

    //음성 인식용
    Intent intent;
    SpeechRecognizer speechRecognizer;

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        searchButton = view.findViewById(R.id.searchButton);
        micButton = view.findViewById(R.id.micButton);
        helpButton = view.findViewById(R.id.helpButton);
        itemNum = view.findViewById(R.id.itemNum);
        title = view.findViewById(R.id.title);
        editSearch = view.findViewById(R.id.editSearch);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        db = AppDatabase.getInstance(this.getContext());

        pref = new SharedPreference(getContext());

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 구분선
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter();

        adapter.setItemClickListener(new OnRecoItemClickListener() {
            @Override
            public void OnItemClick(RecommendAdapter.ViewHolder holder, View view, int position) {
                Recommendation item = adapter.getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        // 음성인식
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어 사용
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(listener);

        changeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        micButton.setOnClickListener(this);
        helpButton.setOnClickListener(this);

        editSearch.addTextChangedListener(new TextWatcher() {   // 공고 검색
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {   // 공고 검색
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    title.setVisibility(View.VISIBLE);
                    editSearch.setVisibility(View.GONE);

                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0); //키보드 내리기
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkLastUpdated();     // 최신 업데이트 일시 확인(일치 -> 유지, 불일치 -> 데이터 가져오기)
        checkCertifiLastUpdated();

        ArrayList<ChipList> regions = pref.getArrayPref(SharedPreference.REGION_LIST);
        ArrayList<ChipList> jobs = pref.getArrayPref(SharedPreference.JOB_LIST);
        Log.d("recruit", "onResume: jobs : " + jobs.toString());
        int careerStatus = pref.preferences.getInt(SharedPreference.CAREER_STATUS, 0);
        Log.d("recruit", "onResume: careerStatus : " + careerStatus);
        String workform = pref.preferences.getString(SharedPreference.WORKFORM_STATUS, "A");    //근무형태
        Log.d("recruit", "onResume: workform :" + workform);
        int licenseStatus = pref.preferences.getInt(SharedPreference.LICENSE_STATUS, 0);
        Log.d("recruit", "onResume: licenseStatus : " + licenseStatus);

        List<String> bDongCode = new ArrayList<>(); //지역
        for (ChipList i : regions) {
            if (i.getCode().length() == 2) {    //시도 전체 법정동 코드 가져오기
                List<String> sido = db.BdongDao().getAllSidoCode(i.getCode());
                for (String j : sido) {
                    bDongCode.add(j);
                    Log.d("recruit", "onResume: bDongCode : " + j);
                }
            } else if (i.getCode().length() == 5) {  //시군구 전체 법정도 코드 가져오기
                List<String> sigungu = db.BdongDao().getAllSigunguCode(i.getCode());
                for (String j : sigungu) {
                    bDongCode.add(j);
                    Log.d("recruit", "onResume: bDongCode : " + j);
                }
            } else {  //해당 법정동 코드 가져오기
                bDongCode.add(i.getCode());
                Log.d("recruit", "onResume: bDongCode : " + i.getCode());
            }
        }

        List<String> jobCode = new ArrayList<>();   //직종
        for (ChipList i : jobs) {
            if (i.getCode().length() == 2) {    //1차분류 전체 직종코드 가져오기
                List<String> allJob = db.jobCategoryDao().getAllJobCode(i.getCode());
                for (String j : allJob) {
                    jobCode.add(j);
                }
            } else {  //해당 직종코드 가져오기
                jobCode.add(i.getCode());
            }
        }
        for (String i : jobCode) {
            Log.d("recruit", "onResume: jobCode : " + i);
        }

        List<String> careerJobTmp = pref.getStringArrayPref(SharedPreference.CAREER_JOB_CODE);
        String careerJobCode = "";
        if (!careerJobTmp.isEmpty()) {
            careerJobCode = careerJobTmp.get(0);
        }
        List<String> careerTmp = pref.getStringArrayPref(SharedPreference.CAREER_PERIOD);
        int career = 0;
        if (!careerTmp.isEmpty()) {
            if (!careerTmp.get(0).equals("")) {
                career = Integer.valueOf(careerTmp.get(0));
            }
        }
        List<String> certificateTmp = pref.getStringArrayPref(SharedPreference.CERTIFICATE_CODE);
        List<String> certificate = new ArrayList<>();
        for (String i : certificateTmp) {
            if (!i.equals("")) {
                certificate.add(i);
            }
        }
//        List<Recruit> result = new ArrayList<>();

        if (careerStatus == 0 && licenseStatus == 0) {    //경력, 자격증 적용안함
            if (bDongCode.size() == 0) {  //지역 선택 안했을 때 -> 전체 지역
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 직종
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getAll().observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                loadListData(recruits);     // LiveData - 데이터 변경을 감지하면 UI 갱신
                            }
                        });
                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList1(workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao1" + recruits.toString());
                                loadListData(recruits);
                            }
                        });
                    }
                } else {  //직종 선택했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList2(jobCode).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao2" + recruits.toString());
                                loadListData(recruits);
                            }
                        });
                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList3(jobCode, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao3" + recruits.toString());
                                loadListData(recruits);
                            }
                        });
                    }
                }
            } else {  //지역 선택 했을 때
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 지역
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > 999) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / 999;
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * 999 + 999 > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * 999, bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * 999, i * 999 + 999);
                                }
                                db.RecruitDao().getFilteredList4(tmp).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao4" + result.toString());
                            loadListData(result);
                        } else {
                            db.RecruitDao().getFilteredList4(bDongCode).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao4" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList4(bDongCode);


                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > 998) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / 998;
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * 998 + 998 > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * 998, bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * 998, i * 998 + 998);
                                }
                                Log.d("many", "onResume: " + tmp.size());
                                db.RecruitDao().getFilteredList5(tmp, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao5" + result.toString());
                            loadListData(result);
                        } else {
                            db.RecruitDao().getFilteredList5(bDongCode, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao5" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList5(bDongCode,workform);

                    }

                } else {  //직종 선택 했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (999 - jobCode.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (999 - jobCode.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (999 - jobCode.size()) + (999 - jobCode.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (999 - jobCode.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (999 - jobCode.size()), i * (999 - jobCode.size()) + (999 - jobCode.size()));
                                }
                                db.RecruitDao().getFilteredList6(tmp, jobCode).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao6" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList6(bDongCode, jobCode).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao6" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList6(bDongCode,jobCode);


                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (998 - jobCode.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (998 - jobCode.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (998 - jobCode.size()) + (998 - jobCode.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (998 - jobCode.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (998 - jobCode.size()), i * (998 - jobCode.size()) + (998 - jobCode.size()));
                                }
                                db.RecruitDao().getFilteredList7(tmp, jobCode, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao7" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList7(bDongCode, jobCode, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao7" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList7(bDongCode,jobCode,workform);

                    }

                }

            }
        } else if (licenseStatus == 0) {    //경력 적용, 자격증 적용 안함
            if (bDongCode.size() == 0) {  //지역 선택 안했을 때 -> 전체 지역
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 직종
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList8(careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao8" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList9(careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao9" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                } else {  //직종 선택했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList10(jobCode, careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao10" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList11(jobCode, careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao11" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                }
            } else {  //지역 선택 했을 때
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 지역
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > 997) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / 997;
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * 997 + 997 > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * 997, bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * 997, i * 997 + 997);
                                }
                                db.RecruitDao().getFilteredList12(tmp, careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao12" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList12(bDongCode, careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao12" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList12(bDongCode, careerJobCode, career);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > 996) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / 996;
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * 996 + 996 > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * 996, bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * 996, i * 996 + 996);
                                }
                                db.RecruitDao().getFilteredList13(tmp, careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao13" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList13(bDongCode, careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao13" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList13(bDongCode, careerJobCode, career, workform);

                    }

                } else {  //직종 선택 했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (997 - jobCode.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (997 - jobCode.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (997 - jobCode.size()) + (997 - jobCode.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (997 - jobCode.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (997 - jobCode.size()), i * (997 - jobCode.size()) + (997 - jobCode.size()));
                                }
                                db.RecruitDao().getFilteredList14(tmp, jobCode, careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao14" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList14(bDongCode, jobCode, careerJobCode, career).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao14" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList14(bDongCode, jobCode, careerJobCode, career);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (996 - jobCode.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (996 - jobCode.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (996 - jobCode.size()) + (996 - jobCode.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (996 - jobCode.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (996 - jobCode.size()), i * (996 - jobCode.size()) + (996 - jobCode.size()));
                                }
                                db.RecruitDao().getFilteredList15(tmp, jobCode, careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao15" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList15(bDongCode, jobCode, careerJobCode, career, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao15" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList15(bDongCode, jobCode, careerJobCode, career, workform);

                    }

                }

            }

        } else if (careerStatus == 0) {   //경력 적용 안함, 자격증 적용
            if (bDongCode.size() == 0) {  //지역 선택 안했을 때 -> 전체 지역
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 직종
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList16(certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao16" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList17(certificate, workform).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao17" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                } else {  //직종 선택했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList18(jobCode, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao18" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList19(jobCode, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao19" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                }
            } else {  //지역 선택 했을 때
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 지역
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (999 - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (999 - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (999 - certificate.size()) + (999 - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (999 - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (999 - certificate.size()), i * (999 - certificate.size()) + (999 - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList20(tmp, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao20" + result.toString());
                            loadListData(result);
                        } else {
                            db.RecruitDao().getFilteredList20(bDongCode, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao20" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList20(bDongCode, certificate);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (998 - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (998 - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (998 - certificate.size()) + (998 - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (998 - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (998 - certificate.size()), i * (998 - certificate.size()) + (998 - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList21(tmp, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao21" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList21(bDongCode, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao21" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList21(bDongCode, workform, certificate);

                    }

                } else {  //직종 선택 했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (999 - jobCode.size() - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (999 - jobCode.size() - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (999 - jobCode.size() - certificate.size()) + (999 - jobCode.size() - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (999 - jobCode.size() - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (999 - jobCode.size() - certificate.size()), i * (999 - jobCode.size() - certificate.size()) + (999 - jobCode.size() - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList22(tmp, jobCode, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao22" + result.toString());
                            loadListData(result);
                        } else {
                            db.RecruitDao().getFilteredList22(bDongCode, jobCode, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao22" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList22(bDongCode, jobCode, certificate);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (998 - jobCode.size() - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (998 - jobCode.size() - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (998 - jobCode.size() - certificate.size()) + (998 - jobCode.size() - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (998 - jobCode.size() - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (998 - jobCode.size() - certificate.size()), i * (998 - jobCode.size() - certificate.size()) + (998 - jobCode.size() - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList23(tmp, jobCode, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }

                        } else {
                            db.RecruitDao().getFilteredList23(bDongCode, jobCode, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao23" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList23(bDongCode, jobCode, workform, certificate);

                    }

                }

            }

        } else {  //경력 적용, 자격증 적용
            if (bDongCode.size() == 0) {  //지역 선택 안했을 때 -> 전체 지역
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 직종
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList24(careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao24" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList25(careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao25" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                } else {  //직종 선택했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        db.RecruitDao().getFilteredList26(jobCode, careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao26" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        db.RecruitDao().getFilteredList27(jobCode, careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                Log.d("recruit", "onResume: dao27" + recruits.toString());
                                loadListData(recruits);
                            }
                        });

                    }
                }
            } else {  //지역 선택 했을 때
                if (jobs.size() == 0) {   //직종 선택 안했을 때 -> 전체 지역
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (997 - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (997 - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (997 - certificate.size()) + (997 - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (997 - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (997 - certificate.size()), i * (997 - certificate.size()) + (997 - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList28(tmp, careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao28" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList28(bDongCode, careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao28" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList28(bDongCode, careerJobCode, career, certificate);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (996 - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (996 - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (996 - certificate.size()) + (996 - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (996 - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (996 - certificate.size()), i * (996 - certificate.size()) + (996 - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList29(tmp, careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList29(bDongCode, careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList29(bDongCode, careerJobCode, career, workform, certificate);

                    }

                } else {  //직종 선택 했을 때
                    if (workform.equals("A")) {   //근무형태 전체 선택
                        if (bDongCode.size() > (997 - jobCode.size() - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (997 - jobCode.size() - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (997 - jobCode.size() - certificate.size()) + (997 - jobCode.size() - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (997 - jobCode.size() - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (997 - jobCode.size() - certificate.size()), i * (997 - jobCode.size() - certificate.size()) + (997 - jobCode.size() - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList30(tmp, jobCode, careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao30" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList30(bDongCode, jobCode, careerJobCode, career, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao30" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList30(bDongCode, jobCode, careerJobCode, career, certificate);

                    } else {  //근무형태 선택했을 때 (정규직 or 계약직)
                        if (bDongCode.size() > (996 - jobCode.size() - certificate.size())) {
                            List<Recruit> result = new ArrayList<>();
                            int n = bDongCode.size() / (996 - jobCode.size() - certificate.size());
                            for (int i = 0; i <= n; i++) {
                                List<String> tmp;
                                if (i * (996 - jobCode.size() - certificate.size()) + (996 - jobCode.size() - certificate.size()) > bDongCode.size()) {
                                    tmp = bDongCode.subList(i * (996 - jobCode.size() - certificate.size()), bDongCode.size());
                                } else {
                                    tmp = bDongCode.subList(i * (996 - jobCode.size() - certificate.size()), i * (996 - jobCode.size() - certificate.size()) + (996 - jobCode.size() - certificate.size()));
                                }
                                db.RecruitDao().getFilteredList31(tmp, jobCode, careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                    @Override
                                    public void onChanged(List<Recruit> recruits) {
                                        result.addAll(recruits);
                                    }
                                });
                            }
                            Log.d("recruit", "onResume: dao31" + result.toString());
                            loadListData(result);

                        } else {
                            db.RecruitDao().getFilteredList31(bDongCode, jobCode, careerJobCode, career, workform, certificate).observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                                @Override
                                public void onChanged(List<Recruit> recruits) {
                                    Log.d("recruit", "onResume: dao31" + recruits.toString());
                                    loadListData(recruits);
                                }
                            });
                        }
//                        result = db.RecruitDao().getFilteredList31(bDongCode, jobCode, careerJobCode, career, workform, certificate);

                    }

                }

            }

        }

    }

    @Override
    public void onClick(View v) {
        if (v == changeButton) {
            Intent intent = new Intent(this.getContext(), FilteringActivity.class);
            startActivity(intent);
        } else if (v == searchButton) {
            if (title.getVisibility() == View.VISIBLE) {
                title.setVisibility(View.GONE);
                helpButton.setVisibility(View.GONE);
                editSearch.setVisibility(View.VISIBLE);
                micButton.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.VISIBLE);
                helpButton.setVisibility(View.VISIBLE);
                editSearch.setVisibility(View.GONE);
                micButton.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            }
        } else if (v == micButton) {
            System.out.println("음성인식 시작!");
            // 권한을 허용하지 않는 경우
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else { // 권한을 허용한 경우
                try {
                    speechRecognizer.startListening(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == helpButton) {
            Intent intent = new Intent(this.getContext(), HelpActivity.class);
            intent.putExtra("from", "RecommendFragment");
            startActivity(intent);
        }
    }

    // 음성인식을 위한 메소드
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(context, "지금부터 말을 해주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            Toast.makeText(context, "천천히 다시 말해 주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            editSearch.setText(rs[0]);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    // 음성인식 어플이 종료되지 않아 계속 실행되는 경우를 막기위해 어플 종료 함수
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer.cancel();
            speechRecognizer = null;
        }
    }

    private void checkLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = null;//db.RecruitDao().getLastUpdated();
        try {
            rows = new RecruitLastUpdateAsyncTask(db.RecruitDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String lastUpdated = rows.get(0);
        Log.d("date: ", "recruit: " + lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP) + "recruit_lastupdated.php";
        URLConnector urlConnector = new URLConnector(php);

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            dbLastUpdated = jsonArray.getJSONObject(0).getString("last_updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!lastUpdated.equals(dbLastUpdated)) {   // 최신 업데이트 일시 확인(불일치 -> 데이터 가져오기)
            String recruitPhp = getResources().getString(R.string.serverIP) + "recruit_update.php?last_updated=" + lastUpdated;
            URLConnector urlConnectorRecruit = new URLConnector(recruitPhp);

            urlConnectorRecruit.start();
            try {
                urlConnectorRecruit.join();
            } catch (InterruptedException e) {
            }
            String recruitResult = urlConnectorRecruit.getResult();

            try {
                JSONObject jsonObject = new JSONObject(recruitResult);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("deleted").equals("0")) {     // 새로 생긴 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        String title = jsonObject1.getString("title");
                        String organization = jsonObject1.getString("organization");
                        String salary_type_code = jsonObject1.getString("salary_type_code");
                        String salary = jsonObject1.getString("salary");
                        String b_dong_code = jsonObject1.getString("b_dong_code");
                        String job_code = jsonObject1.getString("job_code");
                        String career_required = jsonObject1.getString("career_required");
                        int career_min = jsonObject1.getInt("career_min");
                        String enrollment_code = jsonObject1.getString("enrollment_code");
                        String certificate_required = jsonObject1.getString("certificate_required");
                        String x = jsonObject1.getString("x");
                        String y = jsonObject1.getString("y");
                        String update_dt = jsonObject1.getString("update_dt");
                        Recruit newRecruit = new Recruit(recruit_id, title, organization, salary_type_code, salary, b_dong_code, job_code, career_required, career_min, enrollment_code, certificate_required, x, y, update_dt);
                        new RecruitInsertAsyncTask(db.RecruitDao()).execute(newRecruit);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        new RecruitDeleteAsyncTask(db.RecruitDao()).execute(jsonObject1.getString("recruit_id"));   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 북마크 업데이트 (삭제된 공고 제거)
            ArrayList<String> arrayList = new ArrayList<>();
            String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_read.php?user_id=" + getResources().getString(R.string.user_id);
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String recruit_id = jsonObject1.getString("recruit_id");
                    arrayList.add(recruit_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < arrayList.size(); j++) {
                String rId = arrayList.get(j);
                List<Recruit> recruits = null;
                try {
                    recruits = new RecruitGetListAsyncTask(db.RecruitDao()).execute(rId).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (recruits.size() == 0) { // 삭제된 공고면 북마크 테이블에서 해당 공고 삭제
                    String bookmarkDelPhp = context.getResources().getString(R.string.serverIP) + "bookmark_del.php?user_id=" + getResources().getString(R.string.user_id) + "&recruit_id=" + rId;
                    URLConnector urlConnectorBookmarkDel = new URLConnector(bookmarkDelPhp);
                    urlConnectorBookmarkDel.start();
                    try {
                        urlConnectorBookmarkDel.join();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private void checkCertifiLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = null;
        try {
            rows = new CertifiLastUpdateAsyncTask(db.recruitCertificateDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        db.recruitCertificateDao().getLastUpdated();
        String lastUpdated = rows.get(0);
        Log.d("date: ", "certifi: " + lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP) + "recruit_lastupdated.php";
        URLConnector urlConnector = new URLConnector(php);

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            dbLastUpdated = jsonArray.getJSONObject(0).getString("last_updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!lastUpdated.equals(dbLastUpdated)) {   // 최신 업데이트 일시 확인(불일치 -> 데이터 가져오기)
            String recruitCertificatePhp = getResources().getString(R.string.serverIP) + "certificate_update.php?last_updated=" + lastUpdated;
            URLConnector urlConnectorRecruitCertificate = new URLConnector(recruitCertificatePhp);

            urlConnectorRecruitCertificate.start();
            try {
                urlConnectorRecruitCertificate.join();
            } catch (InterruptedException e) {
            }
            String recruitCertificateResult = urlConnectorRecruitCertificate.getResult();

            try {
                JSONObject jsonObject = new JSONObject(recruitCertificateResult);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("deleted").equals("0")) {     // 새로 생긴 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new CertifiInsertAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new CertifiDeleteAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class RecruitGetListAsyncTask extends AsyncTask<String, Void, List<Recruit>> {
        private RecruitDao mRecruitDao;

        public RecruitGetListAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override
        protected List<Recruit> doInBackground(String... strings) {
            return mRecruitDao.getList(strings[0]);
        }
    }

    public static class RecruitLastUpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private RecruitDao mRecruitDao;

        public RecruitLastUpdateAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mRecruitDao.getLastUpdated();
        }

    }

    public static class CertifiLastUpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiLastUpdateAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mRecruitCertifiDao.getLastUpdated();
        }
    }

    // 메인스레드에서 데이터베이스에 접근할 수 없으므로 AsyncTask 사용 - INSERT
    public static class RecruitInsertAsyncTask extends AsyncTask<Recruit, Void, Void> {
        private RecruitDao mRecruitDao;

        public RecruitInsertAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(Recruit... recruits) {
            mRecruitDao.insertNewRecruit(recruits[0]);
            return null;
        }
    }

    public static class CertifiInsertAsyncTask extends AsyncTask<RecruitCertificate, Void, Void> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiInsertAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(RecruitCertificate... recruits) {
            mRecruitCertifiDao.insertNewRecruit(recruits[0]);
            return null;
        }
    }

    // 메인스레드에서 데이터베이스에 접근할 수 없으므로 AsyncTask 사용 - DELETE
    public static class RecruitDeleteAsyncTask extends AsyncTask<String, Void, Void> {
        private RecruitDao mRecruitDao;

        public RecruitDeleteAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(String... strings) {
            mRecruitDao.deleteGoneRecruit(strings[0]);
            return null;
        }
    }

    public static class CertifiDeleteAsyncTask extends AsyncTask<RecruitCertificate, Void, Void> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiDeleteAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(RecruitCertificate... recruitCertificates) {
            mRecruitCertifiDao.deleteGoneRecruit(recruitCertificates[0]);
            return null;
        }
    }

    private void loadListData(List<Recruit> result) { // 항목을 로드하는 함수
        List<Recruit> rows = result;

        allItems.clear();
        items = new ArrayList<>();

        itemNum.setText(String.valueOf(rows.size()));

        // 북마크 테이블 읽어오기
        ArrayList<String> arrayList = new ArrayList<>();
        String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_read.php?user_id=" + getResources().getString(R.string.user_id);
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

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String recruit_id = jsonObject1.getString("recruit_id");
                arrayList.add(recruit_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < rows.size(); i++) {
            Recruit row = rows.get(i);
            int flag = 0;
            String salaryType = new String();
            switch (row.salary_type_code) {     // 급여 타입에 알맞은 단어
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
            String[] tmp = row.salary.split(" ~ ");
            if (tmp.length == 1 || tmp[0].equals(tmp[1])) {
                String[] tmp2 = tmp[0].split("만원|원");
                sal = tmp2[0];
            } else {
                String[] tmp2 = tmp[0].split("만원|원");
                String[] tmp3 = tmp[1].split("만원|원");
                sal = tmp2[0] + " ~ " + tmp3[0];
            }
            DistanceCalculator distance = new DistanceCalculator("127.12934", "35.84688", row.x_coordinate, row.y_coordinate);
            Double dist = distance.getStraightDist();   // 직선거리 구하는 함수

            // 북마크 확인하는 코드
            for (int j = 0; j < arrayList.size(); j++) {
                String rId = arrayList.get(j);
                if (rId.equals(row.recruit_id)) {
                    flag = 1;
                }
            }

            if (flag == 1) {    // 북마크가 되어 있을 때
                allItems.add(new Recommendation(row.recruit_id, row.organization, row.recruit_title, salaryType, sal, dist, true));
            } else {    // 북마크가 안 되어 있을 때
                allItems.add(new Recommendation(row.recruit_id, row.organization, row.recruit_title, salaryType, sal, dist, false));
            }
        }
        Collections.sort(allItems);    // 거리순으로 정렬
        items.addAll(allItems);
        adapter.setItems(items);
        recyclerView.setAdapter(adapter);
    }

    public void search(String text) {    //공고 제목 또는 회사명으로 검색하는 함수
        items.clear();
        if (text.length() == 0) { //검색어 없을 때 전체 데이터 보여줌
            items.addAll(allItems);
        } else {  //검색어 있을 때 검색어를 포함하는 데이터만 보여줌
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getTitle().contains(text) || allItems.get(i).getCompanyName().contains(text)) {
                    items.add(allItems.get(i));
                }
            }
        }
        itemNum.setText(String.valueOf(items.size()));
        adapter.notifyDataSetChanged();
    }
}