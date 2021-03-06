package io.rong.imkit.conversation.messgelist.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.activity.PicturePagerActivity;
import io.rong.imkit.feature.resend.ResendManager;
import io.rong.imkit.model.State;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.picture.tools.ScreenUtils;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;

import static io.rong.imkit.conversation.messgelist.provider.SightMessageItemProvider.dip2pix;

public class ImageMessageItemProvider extends BaseMessageItemProvider<ImageMessage> {
    private final String MSG_TAG = "RC:ImgMsg";
    private static final String TAG = "ImageMessageItemProvide";
    private Integer minSize = null;
    private Integer maxSize = null;
    private static int THUMB_COMPRESSED_SIZE = 240;
    private static int THUMB_COMPRESSED_MIN_SIZE = 100;

    public ImageMessageItemProvider() {
        mConfig.showContentBubble = false;
        mConfig.showProgress = false;
        Context context = IMCenter.getInstance().getContext();
        if (context != null) {
            Resources resources = context.getResources();
            try {
                THUMB_COMPRESSED_SIZE = resources.getInteger(resources.getIdentifier("rc_thumb_compress_size", "integer", context.getPackageName()));
                THUMB_COMPRESSED_MIN_SIZE = resources.getInteger(resources.getIdentifier("rc_thumb_compress_min_size", "integer", context.getPackageName()));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_image_message_item, parent, false);
        return new ViewHolder(view.getContext(), view);
    }


    @Override
    protected void bindMessageContentViewHolder(final ViewHolder holder, ViewHolder parentHolder, ImageMessage message, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        final ImageView view = holder.getView(R.id.rc_image);
        Uri thumUri = message.getThumUri();
        if (uiMessage.getState() == State.PROGRESS
                || (uiMessage.getState() == State.ERROR && ResendManager.getInstance().needResend(uiMessage.getMessageId()))) {
            holder.setVisible(R.id.rl_progress, true);
            holder.setVisible(R.id.main_bg, true);
            holder.setText(R.id.tv_progress, uiMessage.getProgress() + "%");
        } else {
            holder.setVisible(R.id.rl_progress, false);
            holder.setVisible(R.id.main_bg, false);
        }
        if (thumUri != null && thumUri.getPath() != null) {
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(dip2pix(IMCenter.getInstance().getContext(), 6))).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            Glide.with(view).load(thumUri.getPath())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            measureLayoutParams(holder.getView(R.id.rl_content), resource);
                            return false;
                        }
                    })
                    .into(view);
        }

    }

    @Override
    protected boolean onItemClick(ViewHolder holder, ImageMessage imageMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        Intent intent = new Intent(holder.getContext(), PicturePagerActivity.class);
        intent.putExtra("message", uiMessage.getMessage());
        holder.getContext().startActivity(intent);
        return true;
    }

    //???????????????????????? 100 X 100??????????????? 240 X 240
    // ??????????????????????????????
    // 1??????????????????????????? 100 ????????????20 X 40 ??????????????????????????????????????? 100 ?????????????????????????????????240 ?????????????????? 240
    // ????????????
    // 2?????????????????? 240 ???????????? 100 ????????????120 X 140 ??????????????????????????????????????? 240 ????????????
    // 3???????????????????????????240????????????????????????
    //(1?????????????????????????????? 2.4??????????????????????????? 240 ???????????????
    //(2???????????????????????? 2.4??????????????????????????????????????????????????? 100????????????????????? 240 ???????????????
    private void measureLayoutParams(View view, Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (minSize == null) {
            minSize = THUMB_COMPRESSED_MIN_SIZE;
        }
        if (maxSize == null) {
            maxSize = THUMB_COMPRESSED_SIZE;
        }
        int finalWidth;
        int finalHeight;
        if (width < minSize || height < minSize) {
            if (width < height) {
                finalWidth = minSize;
                finalHeight = Math.min((int) (minSize * 1f / width * height), maxSize);
            } else {
                finalHeight = minSize;
                finalWidth = Math.min((int) (minSize * 1f / height * width), maxSize);
            }
        } else if (width < maxSize && height < maxSize) {
            finalWidth = width;
            finalHeight = height;
        } else {
            if (width > height) {
                if (width * 1f / height <= maxSize * 1.0f / minSize) {
                    finalWidth = maxSize;
                    finalHeight = (int) (maxSize * 1f / width * height);
                } else {
                    finalWidth = maxSize;
                    finalHeight = minSize;
                }
            } else {
                if (height * 1f / width <= maxSize * 1.0f / minSize) {
                    finalHeight = maxSize;
                    finalWidth = (int) (maxSize * 1f / height * width);
                } else {
                    finalHeight = maxSize;
                    finalWidth = minSize;
                }
            }
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ScreenUtils.dip2px(view.getContext(), finalHeight / 2);
        params.width = ScreenUtils.dip2px(view.getContext(), finalWidth / 2);
        view.setLayoutParams(params);
    }


    @Override
    public Spannable getSummarySpannable(Context context, ImageMessage imageMessage) {
        return new SpannableString(context.getString(R.string.rc_conversation_summary_content_image));
    }


    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof ImageMessage && !messageContent.isDestruct();
    }
}
