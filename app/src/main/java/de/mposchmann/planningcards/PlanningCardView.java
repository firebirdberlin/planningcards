package de.mposchmann.planningcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;

public class PlanningCardView extends AbstractCardView {

    private final String text;

    private float adjustedTextSize = 0;

    private final float minScalePercent = 0.05f;
    private final float maxScalePercent = 0.4f;

    private float currentScalePercent = 0.2f;
    private boolean scaleChanged = true;

    private boolean hide = false;

    public void zoom(float factor) {
        currentScalePercent = Math.max(minScalePercent, Math.min(maxScalePercent, currentScalePercent * factor));
        scaleChanged = true;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
        invalidate();
    }

    public String getText() {
        return text;
    }

    public PlanningCardView(Context context, String text) {
        super(context);
        paint.setColor(Color.BLACK);
        this.setBackgroundColor(Color.BLACK);
        this.text = text;

    }

    @Override
    protected void drawCardContent(Canvas canvas, float margin, RectF frameRect) {
        drawNumbers(canvas, margin, frameRect);
    }

    private void drawNumbers(Canvas canvas, float margin, RectF frameRect) {
        // use densityMultiplier to take into account different pixel densities
        /*
         * final float densityMultiplier = getContext().getResources()
         * .getDisplayMetrics().density;
         * paint.setTextSize(24.0f*densityMultiplier);
         */

        //draw text
        paint.setStyle(Paint.Style.FILL);

        if (this.adjustedTextSize == 0 || scaleChanged) {

            //get minimum allowed text size:
            //float minTextSize = adjustTextSize(text, canvas, .05f);

            //this.adjustedTextSize = adjustTextSize(text, canvas, .3f);

            //calc max scale factor by percent but limit to frameRect
            float maxTextSizeByPercent = adjustTextSize(text, canvas, currentScalePercent);
            float maxTextSize = getMaxTextSize(text, frameRect);
            this.adjustedTextSize = Math.min(maxTextSize, maxTextSizeByPercent);

            paint.setTextSize(adjustedTextSize);
            scaleChanged = false;
            //Log.d("drawview", "adjustTextSize called");
        } else {
            paint.setTextSize(adjustedTextSize);
        }

        /*
        paint.setTextAlign(Align.CENTER);
        if (posX == 0) {
            this.posX = (canvas.getWidth() / 2);
        }
        if (posY == 0) {
            this.posY = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        }
        canvas.drawText(text, 0, text.length(), posX, posY, paint);
        */

        //draw number centered
        paint.setTextAlign(Align.LEFT);
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float textCenterX = ((canvas.getWidth() - textBounds.width()) / 2f) - textBounds.left;  //must subtract left which is the hor. offset
        float centerY = ((canvas.getHeight() - textBounds.height()) / 2f) - textBounds.bottom;  //must subtract bottom which is the fonts descent
        float textCenterY = centerY + textBounds.height(); //bottom origin
        canvas.drawText(text, textCenterX, textCenterY, paint);

        final Rect tmpBounds = new Rect();

        //draw small text (half the size) again in all 4 corners
        paint.setTextSize(adjustedTextSize * 0.3f);
        paint.getTextBounds(text, 0, text.length(), tmpBounds);
        paint.setTextAlign(Align.LEFT);

        //canvas.drawRect(30f, 30f, 30f + tmpBounds.width(), 30f + tmpBounds.height(), paint);

        //top left
        margin *= 1.8f;
        canvas.drawText(text, margin - tmpBounds.left, margin - tmpBounds.bottom + tmpBounds.height(), paint);

        //top right
        canvas.drawText(text, this.getWidth() - margin - tmpBounds.left - tmpBounds.width() - 1, margin - tmpBounds.bottom  + tmpBounds.height(), paint);

        //bottom left - rotated by 180 degree
        canvas.save();
        canvas.rotate(180, margin + (tmpBounds.width() / 2), this.getHeight() - margin - (tmpBounds.height() / 2) - 1);
        canvas.drawText(text, margin - tmpBounds.left, this.getHeight() - margin - tmpBounds.bottom -1 , paint);
        canvas.restore();

        //bottom right - rotated by 180 degree
        canvas.save();
        canvas.rotate(180,  this.getWidth() - margin - (tmpBounds.width() / 2) - 1, this.getHeight() - margin - (tmpBounds.height() / 2) - 1);
        canvas.drawText(text, this.getWidth() - margin - tmpBounds.left - tmpBounds.width() - 1, this.getHeight() - margin - tmpBounds.bottom -1 , paint);
        canvas.restore();
    }

    private float adjustTextSize(String text, Canvas canvas, float percent) {
        paint.setTextSize(100);
        paint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this
        // text
        paint.getTextBounds(text, 0, text.length(), bounds);

        // get the height that would have been produced
        int h = bounds.bottom - bounds.top;

        // make the text text up 70% of the height
        float target = (float) canvas.getHeight() * percent;

        // figure out what textSize setting would create that height
        // of text
        float size = ((target / h) * 100f);

        // and set it into the paint
        paint.setTextSize(size);

        return size;
    }

    private float getMaxTextSize(String text, RectF boundingRect) {
        paint.setTextSize(100);
        paint.setTextScaleX(1.0f);
        Rect textBounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this
        // text
        paint.getTextBounds(text, 0, text.length(), textBounds);

        //get scale factor so that text-bounds fits into boundingRect
        float scale = Math.min(boundingRect.height() / textBounds.height(), boundingRect.width() / textBounds.width()) * 100f;

        // and set it into the paint
        paint.setTextSize(scale);

        return scale;
    }

}
