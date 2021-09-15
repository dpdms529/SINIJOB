package org.techtown.hanieum;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.CvInfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CareerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Career> items = new ArrayList<>();
    Context context;
    AppDatabase db;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.career_item, viewGroup, false);
        context = inflater.getContext();
        db = AppDatabase.getInstance(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Career item = items.get(position);
        ((ViewHolder) viewHolder).setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addItem(Career item) {
        items.add(item);
    }

    public void setItems(ArrayList<Career> items) {
        this.items = items;
    }

    public Career getItem(int position) {
        return items.get(position);
    }

    public ArrayList<Career> getItems() {
        return items;
    }

    public void setItem(int position, Career item) {
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView delete;
        EditText compName;
        TextView period;

        int n = 0;
        Calendar cal = Calendar.getInstance();
        int startY = cal.get(Calendar.YEAR);
        int startM = cal.get(Calendar.MONTH) + 1;
        int endY = cal.get(Calendar.YEAR);
        int endM = cal.get(Calendar.MONTH) + 1;
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                if (n == 0 || n == 2) { // 첫번째 date 또는 입력 완료
                    startY = year;
                    startM = monthOfYear;
                    if (monthOfYear/10 == 1) {
                        period.setText(year + "-" + monthOfYear);
                    } else {
                        period.setText(year + "-0" + monthOfYear);
                    }

                    if (n == 2) {
                        n = 0;
                    }
                    n++;
                } else if (n == 1) {    // 두번째 date
                    endY = year;
                    endM = monthOfYear;
                    if (monthOfYear/10 == 1) {
                        period.setText(period.getText() + " ~ " + year + "-" + monthOfYear);
                    } else {
                        period.setText(period.getText() + " ~ " + year + "-0" + monthOfYear);
                    }
                    n++;

                    // 시작 날짜와 끝 날짜 개월 수 구하기
                    Calendar start = new GregorianCalendar(startY, startM, 1);
                    Calendar end = new GregorianCalendar(endY, endM, 1);
                    long diffSec = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
                    long diffDay = diffSec / (24*60*60);
                    int diffMon = (int) diffDay / 30 + 1;

                    // 시작 날짜보다 끝 날짜가 이르면
                    if (diffMon <= 0) {
                        Toast.makeText(context, "올바르지 않습니다", Toast.LENGTH_LONG).show();
                        period.setText("기간을 설정하세요");
                        startY = cal.get(Calendar.YEAR);
                        startM = cal.get(Calendar.MONTH) + 1;
                        endY = cal.get(Calendar.YEAR);
                        endM = cal.get(Calendar.MONTH) + 1;
                    }
                }
            }
        };

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            compName = view.findViewById(R.id.compName);
            period = view.findViewById(R.id.period);

            delete.setOnClickListener(this);
            period.setOnClickListener(this);

            compName.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 입력에 변화가 있을 때
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                    // 입력이 끝났을 때
                    items.get(getAdapterPosition()).setCompName(compName.getText().toString());
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 입력하기 전
                }
            });
        }

        public void setItem(Career item) {
            compName.setText(item.getCompName());
        }

        @Override
        public void onClick(View v) {
            if (v == delete) {
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context, R.style.MaterialAlertDialog_OK_color)
                        .setTitle("삭제") .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                // 아이템 삭제
                                items.remove(getAdapterPosition());
                                // db에서 삭제
                                new CareerDeleteAsyncTask(db.CvInfoDao()).execute(getAdapterPosition());
                                notifyDataSetChanged();

                                List<CvInfo> cv = null;
                                try {
                                    cv = new CarCerActivity.GetAllAsyncTask(db.CvInfoDao()).execute().get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                for (int j=0; j<cv.size(); j++) {
                                    Log.d("aaaaaa", cv.get(j).company_name);    ////////////////
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog msgDlg = msgBuilder.create();
                msgDlg.show();
            } else if (v == period) {
                MyDatePicker datePicker = new MyDatePicker(startY, startM, endY, endM);
                datePicker.setListener(d);
                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "MyDatePicker");
            }
        }
    }

    public static class CareerDeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CareerDeleteAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mCvInfoDao.deleteCvInfo("CA", integers[0]);
            return null;
        }
    }


//    private void showDialog() {
//        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
//                .setTitle("앱 끈다?") .setMessage("진짜 끈다?")
//                .setPositiveButton("꺼라", new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                    }
//                })
//                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MainActivity.this, "안 끔", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        AlertDialog msgDlg = msgBuilder.create();
//        msgDlg.show();
//    }


//    private final TextWatcher textWatcher = new TextWatcher() {
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // 입력하기 전에 조치
//        }
//
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // 입력난에 변화가 있을 시 조치
//        }
//
//        public void afterTextChanged(Editable s) {
//            // 입력이 끝났을 때 조치
//
//        }
//    };

}
