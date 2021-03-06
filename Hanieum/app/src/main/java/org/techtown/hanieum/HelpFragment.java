package org.techtown.hanieum;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.util.Locale;

public class HelpFragment extends Fragment implements View.OnClickListener {

    Context context;
    int layout;

    ImageButton listenButton;
    TextView text;
    ImageView image;

    //음성 출력용
    TextToSpeech tts;

    public HelpFragment(int layout) {
        this.layout = layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        context = getContext();
        listenButton = view.findViewById(R.id.listenButton);
        text = view.findViewById(R.id.text);
        image = view.findViewById(R.id.image);

        // 동그란 모양으로 변경
        listenButton.setBackground(new ShapeDrawable(new OvalShape()));
        listenButton.setClipToOutline(true);

        // 음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        if (HelpActivity.from.equals("RecommendFragment")) {
            if (layout == 1) {
                Glide.with(this).load(R.drawable.job_list1)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("일자리의 목록과 건수를 확인할 수 있습니다");
            } else if (layout == 2) {
                image.setImageDrawable(context.getDrawable(R.drawable.job_list2));
                text.setText("등록된 주소와 근무지까지의 거리를 볼 수 있고 별표를 눌러 맘에 드는 공고를 즐겨찾기 할 수 있습니다");
            } else if (layout == 3) {
                image.setImageDrawable(context.getDrawable(R.drawable.job_list3));
                text.setText("공고를 눌러 상세정보를 볼 수 있고 원하는 공고를 검색할 수 있습니다");
            } else if (layout == 4) {
                image.setImageDrawable(context.getDrawable(R.drawable.job_list4));
                text.setText("검색어를 직접 입력하거나 마이크 버튼을 눌러 음성으로 검색할 수 있습니다");
            } else if (layout == 5) {
                image.setImageDrawable(context.getDrawable(R.drawable.job_list5));
                text.setText("표시할 공고의 조건을 변경할 수 있습니다");
            }
        } else if (HelpActivity.from.equals("FilteringActivity")) {
            if (layout == 1) {
                Glide.with(this).load(R.drawable.region_sel)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("보고싶은 공고의 지역을 선택할 수 있습니다");
            } else if (layout == 2) {
                Glide.with(this).load(R.drawable.job_sel)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("보고싶은 공고의 직종을 선택할 수 있습니다");
            } else if (layout == 3) {
                Glide.with(this).load(R.drawable.filtering2)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("등록된 이력서의 경력과 자격증에 맞는 공고를 볼지 선택할 수 있습니다\n" +
                        "적용 안함을 하면 모든 경력과 자격증의 공고가 노출됩니다");
            } else if (layout == 4) {
                Glide.with(this).load(R.drawable.filtering3)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("원하는 근무 형태의 공고를 선택할 수 있습니다");
            } else if (layout == 5) {
                Glide.with(this).load(R.drawable.filtering4)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("x를 눌러 삭제할 수 있고 초기화 버튼을 눌러 모든 변경 사항을 없앨 수 있습니다");
            } else if (layout == 6) {
                Glide.with(this).load(R.drawable.filtering5)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(new DrawableImageViewTarget(image));
                text.setText("저장 버튼을 누르면 선택된 조건으로 공고를 볼 수 있습니다");
            }
        } else if (HelpActivity.from.equals("HomeFragment")) {
            if (layout == 1) {
                image.setImageDrawable(context.getDrawable(R.drawable.reco1));
                text.setText("즐겨찾기한 공고와 지원한 공고를 기반으로 15km 이내의 추천된 공고를 확인할 수 있습니다");
            } else if (layout == 2) {
                image.setImageDrawable(context.getDrawable(R.drawable.reco2));
                text.setText("등록된 주소와 근무지까지의 거리를 볼 수 있고 별표를 눌러 맘에 드는 공고를 즐겨찾기 할 수 있습니다");
            } else if (layout == 3) {
                image.setImageDrawable(context.getDrawable(R.drawable.reco3));
                text.setText("공고를 눌러 상세정보를 볼 수 있습니다");
            }
        } else if (HelpActivity.from.equals("DetailActivity")) {
            if (layout == 1) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail1));
                text.setText("공고의 정보를 간략히 확인할 수 있습니다\n별표를 누르면 공고를 즐겨찾기 할 수 있습니다");
            } else if (layout == 2) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail2));
                text.setText("글씨 크기를 조절할 수 있습니다\n음성 출력을 켜 텍스트를 터치해 음성으로 듣거나 '요약해서 듣기' 버튼을 눌러 공고를 요약해 들을 수 있습니다");
            } else if (layout == 3) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail3));
                text.setText("근무지 위치를 확인할 수 있고 '길찾기' 버튼을 눌러 근무지까지의 길을 찾을 수 있습니다");
            } else if (layout == 4) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail4));
                text.setText("버튼 또는 이미지를 눌러 채용정보 제공사이트로 이동할 수 있습니다");
            } else if (layout == 5) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail6));
                text.setText("'공유' 버튼을 눌러 해당 공고를 간략히 공유할 수 있습니다");
            } else if (layout == 6) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail5));
                text.setText("'지원하기' 버튼을 눌러 해당 공고에 지원할 수 있습니다");
            }
        } else {    // ApplyActivity
            if (layout == 1) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail7));
                text.setText("'마이크' 버튼을 누르면 하단에 안내 메시지가 뜨고 음성으로 텍스트를 입력할 수 있습니다\n또한 키보드로 입력할 수 있습니다");
            } else if (layout == 2) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail8));
                text.setText("음성 출력을 키고 텍스트를 터치하면 입력된 텍스트를 음성으로 들을 수 있습니다");
            } else if (layout == 3) {
                image.setImageDrawable(context.getDrawable(R.drawable.detail9));
                text.setText("'완료' 버튼을 눌러 입력된 텍스트와 함께 해당 공고에 지원할 수 있습니다");
            }
        }

        listenButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == listenButton) {
            String msg = text.getText().toString();
            voiceOut(msg);
        }
    }

    // 음성인식 어플이 종료되지 않아 계속 실행되는 경우를 막기위해 어플 종료 함수
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    // 음성 메세지 출력용
    private void voiceOut(String msg) {
        if (msg.length() < 1) return;

        // 음성 출력
        tts.setPitch(0.8f); //목소리 톤1.0
        tts.setSpeechRate(0.9f);    //목소리 속도
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}