package org.techtown.hanieum;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.CvInfo;

import java.util.ArrayList;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        EditText compName;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            compName = view.findViewById(R.id.compName);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context, R.style.MaterialAlertDialog_OK_color)
                            .setTitle("삭제") .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                    // 아이템 삭제
                                    items.remove(getAdapterPosition());
                                    // db에서 삭제
                                    new CareerDeleteAsyncTask(db.CvInfoDao()).execute(getAdapterPosition());
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog msgDlg = msgBuilder.create();
                    msgDlg.show();
                }
            });
            compName.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 입력난에 변화가 있을 시 조치
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                    // 입력이 끝났을 때 조치
                    items.get(getAdapterPosition()).setCompName(compName.getText().toString());
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 입력하기 전에 조치
                }
            });
        }

        public void setItem(Career item) {
            compName.setText(item.getCompName());
        }
    }

    public static class CareerDeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CareerDeleteAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
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
