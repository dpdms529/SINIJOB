package org.techtown.hanieum;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CareerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Career> items = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.career_item, viewGroup, false);
        context = inflater.getContext();
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

    @Override   // 아이템이 뒤섞이는 문제 해결
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
        EditText position;
        TextView period;
        TextView job;

        int n = 0;
        Calendar cal = Calendar.getInstance();
        int startY = cal.get(Calendar.YEAR);
        int startM = cal.get(Calendar.MONTH) + 1;
        int endY = cal.get(Calendar.YEAR);
        int endM = cal.get(Calendar.MONTH) + 1;
        String startStr, endStr;
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (n == 0 || n == 2) { // 첫번째 date 또는 입력 완료
                    startY = year;
                    startM = monthOfYear;
                    if (monthOfYear / 10 == 1) {
                        startStr = year + "-" + monthOfYear;
                        period.setText(year + "-" + monthOfYear);
                    } else {
                        startStr = year + "-0" + monthOfYear;
                        period.setText(year + "-0" + monthOfYear);
                    }

                    if (n == 2) {
                        n = 0;
                    }
                    n++;
                } else if (n == 1) {    // 두번째 date
                    endY = year;
                    endM = monthOfYear;
                    if (monthOfYear / 10 == 1) {
                        endStr = year + "-" + monthOfYear;
                        period.setText(period.getText() + " ~ " + year + "-" + monthOfYear);
                    } else {
                        endStr = year + "-0" + monthOfYear;
                        period.setText(period.getText() + " ~ " + year + "-0" + monthOfYear);
                    }
                    n++;

                    // 시작 날짜와 끝 날짜 개월 수 구하기
                    Calendar start = new GregorianCalendar(startY, startM, 1);
                    Calendar end = new GregorianCalendar(endY, endM, 1);
                    long diffSec = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
                    long diffDay = diffSec / (24 * 60 * 60);
                    int diffMon = (int) diffDay / 30 + 1;

                    // 시작 날짜보다 끝 날짜가 이르면
                    if (diffMon <= 0) {
                        Toast.makeText(context, "올바르지 않습니다", Toast.LENGTH_LONG).show();
                        period.setText("");
                        period.setHint("기간을 설정하세요");
                        items.get(getAdapterPosition()).setCareerStart(null);
                        startY = cal.get(Calendar.YEAR);
                        startM = cal.get(Calendar.MONTH) + 1;
                        endY = cal.get(Calendar.YEAR);
                        endM = cal.get(Calendar.MONTH) + 1;
                    }else{
                        items.get(getAdapterPosition()).setCareerStart(startStr);
                        items.get(getAdapterPosition()).setCarrerEnd(endStr);
                        items.get(getAdapterPosition()).setPeriod(diffMon);
                    }
                }
            }
        };

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            compName = view.findViewById(R.id.compName);
            position = view.findViewById(R.id.position);
            period = view.findViewById(R.id.period);
            job = view.findViewById(R.id.job);

            delete.setOnClickListener(this);
            period.setOnClickListener(this);
            job.setOnClickListener(this);

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

            position.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    items.get(getAdapterPosition()).setPosition(position.getText().toString());
                }
            });
        }

        public void setItem(Career item) {
            if(item.getJobName() == null){
                job.setText("");
                job.setHint("직종을 선택하세요");
            }else{
                job.setText(item.getJobName());
            }
            compName.setText(item.getCompName());
            position.setText(item.getPosition());
            if(item.getCareerStart() == null){
                period.setText("");
                period.setHint("기간을 설정하세요");
            }else{
                period.setText(item.getCareerStart() + " ~ " + item.getCarrerEnd());
            }

        }

        @Override
        public void onClick(View v) {
            if (v == delete) {
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context, R.style.MaterialAlertDialog_OK_color)
                        .setTitle("삭제").setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 아이템 삭제
                                items.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog msgDlg = msgBuilder.create();
                msgDlg.show();
            } else if (v == period) {
                if (items.get(getAdapterPosition()).getCareerStart() != null) {
                    String[] start = items.get(getAdapterPosition()).getCareerStart().split("-");
                    String[] end = items.get(getAdapterPosition()).getCarrerEnd().split("-");
                    startY = Integer.parseInt(start[0]);
                    startM = Integer.parseInt(start[1]);
                    endY = Integer.parseInt(end[0]);
                    endM = Integer.parseInt(end[1]);
                }
                MyDatePicker datePicker = new MyDatePicker(startY, startM, endY, endM);
                datePicker.setListener(d);
                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "MyDatePicker");
            } else if (v == job) {
                JobDialog dialog = new JobDialog(context, items.get(getAdapterPosition()));
                Log.d("TAG", "onClick: " + items.get(getAdapterPosition()).getClass());
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface d) {
                        job.setText(items.get(getAdapterPosition()).getJobName());
                        Log.d("TAG", "onClick: " + items.get(getAdapterPosition()).getJobCode() + items.get(getAdapterPosition()).getJobName());
                    }
                });
            }
        }
    }
}
