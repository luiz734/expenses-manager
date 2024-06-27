package com.example.gerenciadordecustos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class ProfilePicture extends AppCompatImageView {

    private Paint paintBitmap;
    private Paint paintBorder;
    private int borderWidth = 4;

    public ProfilePicture(Context context) {
        super(context);
        init();
    }

    public ProfilePicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfilePicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintBitmap = new Paint();
        paintBitmap.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setColor(getResources().getColor(android.R.color.darker_gray));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(borderWidth);
        paintBorder.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = Math.min(getWidth(), getHeight()) / 2f;

        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paintBitmap.setShader(shader);

            canvas.drawCircle(radius, radius, radius - borderWidth, paintBitmap);
        }

        canvas.drawCircle(radius, radius, radius - borderWidth / 2f, paintBorder);
    }

    private Bitmap getBitmap() {
        if (getDrawable() == null) {
            return null;
        }

        int width = getWidth();
        int height = getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        getDrawable().setBounds(0, 0, width, height);
        getDrawable().draw(canvas);

        return bitmap;
    }
}
