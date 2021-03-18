package com.telit.zhkt_three.Adapter.AutoLearning;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.ebook.FlipEBookResourceActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/10 9:07
 */
public class PullOperationAdapter extends RecyclerView.Adapter<PullOperationAdapter.PullOperationViewHolder> {

    private Context mContext;
    private List<LocalResourceRecord> pullOperationBeans;

    public void setPullOperationBeans(List<LocalResourceRecord> pullOperationBeans) {
        this.pullOperationBeans = pullOperationBeans;
    }

    public PullOperationAdapter(Context context, List<LocalResourceRecord> pullOperationBeans) {
        mContext = context;
        this.pullOperationBeans = pullOperationBeans;
    }

    @NonNull
    @Override
    public PullOperationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new PullOperationViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_auto_learn_pull_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PullOperationViewHolder pullOperationViewHolder, int i) {

        QZXTools.logE("cover=" + pullOperationBeans.get(i).getImageUrl(), null);

        Glide.with(mContext).load(pullOperationBeans.get(i).getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.no_cover)
                .into(pullOperationViewHolder.pull_avatar);

        pullOperationViewHolder.pull_tv_title.setText(pullOperationBeans.get(i).getResourceName());

        pullOperationViewHolder.pull_tv_date.setText(pullOperationBeans.get(i).getResourceUpdateDate());

        if (pullOperationBeans.get(i).getCanChecked()) {
            pullOperationViewHolder.pull_cb.setVisibility(View.VISIBLE);
            pullOperationViewHolder.pull_cb.setChecked(pullOperationBeans.get(i).getIsChoosed());
        } else {
            pullOperationViewHolder.pull_cb.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return pullOperationBeans != null ? pullOperationBeans.size() : 0;
    }

    public class PullOperationViewHolder extends RecyclerView.ViewHolder {

        private ImageView pull_avatar;
        private TextView pull_tv_title;
        private TextView pull_tv_date;
        private CheckBox pull_cb;

        public PullOperationViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (pullOperationBeans.get(getLayoutPosition()).getResourceType()) {
                        case "1":
                            String filePath = pullOperationBeans.get(getLayoutPosition()).getResourceFilePath();
                            if (filePath.substring(filePath.lastIndexOf(".") + 1).equals("zip")) {
                                //解压zip文件
                                String parentDir = QZXTools.getExternalStorageForFiles(mContext, null);
                                String destinationDir = parentDir + File.separator +
                                        pullOperationBeans.get(getLayoutPosition()).getResourceId() + File.separator;
                                File file = new File(destinationDir);
                                QZXTools.logE("destination=" + destinationDir + ";file is exist=" + file.exists(), null);

                                ArrayList<String> imgFilePathList = new ArrayList<>();

                                if (file.exists() && file.isDirectory()) {
                                    File[] files = file.listFiles();
                                    for (File f : files) {
                                        imgFilePathList.add(f.getAbsolutePath());
                                    }
                                } else {
                                    zipFileRead(filePath, destinationDir, imgFilePathList);
                                }

                                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                intent_img.putExtra("curImgIndex", 0);
                                mContext.startActivity(intent_img);
                            } else {
                                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                ArrayList<String> imgFilePathList = new ArrayList<>();
                                imgFilePathList.add(filePath);
                                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                intent_img.putExtra("curImgIndex", 0);
                                mContext.startActivity(intent_img);
                            }
                            break;
                        case "2":
                            Intent intent = new Intent(mContext, AudioPlayActivity.class);
                            intent.putExtra("AudioFilePath", pullOperationBeans.get(getLayoutPosition()).getResourceFilePath());
                            intent.putExtra("AudioFileName", pullOperationBeans.get(getLayoutPosition()).getResourceName());
                            mContext.startActivity(intent);
                            break;
                        case "3":
                            Intent intent_video = new Intent(mContext, VideoPlayerActivity.class);
                            intent_video.putExtra("VideoFilePath", pullOperationBeans.get(getLayoutPosition())
                                    .getResourceFilePath());
                            intent_video.putExtra("VideoTitle", pullOperationBeans.get(getLayoutPosition()).getResourceName());
                            if (!TextUtils.isEmpty(pullOperationBeans.get(getLayoutPosition()).getImageUrl())) {
                                intent_video.putExtra("VideoThumbnail", pullOperationBeans
                                        .get(getLayoutPosition()).getImageUrl());
                            }
                            mContext.startActivity(intent_video);
                            break;
                        case "1010":
                            Intent intent_resource = new Intent(mContext, FlipEBookResourceActivity.class);
                            intent_resource.putExtra("EBookFilePath", pullOperationBeans
                                    .get(getLayoutPosition()).getResourceFilePath());
                            intent_resource.putExtra("CoverUrl", pullOperationBeans.get(getLayoutPosition()).getImageUrl());
                            mContext.startActivity(intent_resource);
                            break;
                        default:
                            QZXTools.openFile(new File(pullOperationBeans.get(getLayoutPosition()).getResourceFilePath()), mContext);
                            break;
                    }

                }
            });

            pull_avatar = itemView.findViewById(R.id.pull_avatar);
            pull_tv_title = itemView.findViewById(R.id.pull_tv_title);
            pull_tv_date = itemView.findViewById(R.id.pull_tv_date);
            pull_cb = itemView.findViewById(R.id.pull_cb);

            pull_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //更改checkbox界面
                    pull_cb.setChecked(isChecked);
                    //回调接口更改删除按钮界面和实体类数据
                    if (checkedInterface != null) {
                        checkedInterface.checkedStatus(isChecked, getAdapterPosition());
                    }
                }
            });
        }
    }

    private CheckedInterface checkedInterface;

    public void setCheckedInterface(CheckedInterface checkedInterface) {
        this.checkedInterface = checkedInterface;
    }

    public interface CheckedInterface {
        void checkedStatus(boolean hasChecked, int position);
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(读取Zip信息 ， 获得zip中所有的目录文件信息)
     * @param设定文件
     */
    public void zipFileRead(String file, String saveRootDirectory, ArrayList<String> imgList) {
        try {
            // 获得zip信息
            ZipFile zipFile = new ZipFile(file, "GBK");
//            @SuppressWarnings("unchecked")
//            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.getEntries();
            while (enu.hasMoreElements()) {
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream read = zipFile.getInputStream(zipElement);
                String fileName = zipElement.getName();
                if (fileName != null && fileName.indexOf(".") != -1) {// 是否为文件
                    unZipFile(zipElement, read, saveRootDirectory, imgList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(找到文件并读取解压到指定目录)
     */
    public void unZipFile(ZipEntry ze, InputStream read,
                          String saveRootDirectory, ArrayList<String> imgList) {
        // 如果只读取图片，自行判断就OK.
        String fileName = ze.getName();
        imgList.add(saveRootDirectory.concat(fileName));

        // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(saveRootDirectory + fileName);
        if (!file.exists()) {
            File rootDirectoryFile = new File(file.getParent());
            // 创建目录
            if (!rootDirectoryFile.exists()) {
                boolean ifSuccess = rootDirectoryFile.mkdirs();
                if (ifSuccess) {
                    System.out.println("文件夹创建成功!");
                } else {
                    System.out.println("文件创建失败!");
                }
            }
            // 创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 写入文件
        BufferedOutputStream write = null;
        try {
            write = new BufferedOutputStream(new FileOutputStream(file));
            int cha = 0;
            while ((cha = read.read()) != -1) {
                write.write(cha);
            }
            // 要注意IO流关闭的先后顺序
            write.flush();
            write.close();
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
