package de.mposchmann.planningcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;

public class WelcomeCardView extends AbstractCardView {

    private Float adjustedTextSize = null;

    public WelcomeCardView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        this.setBackgroundColor(Color.BLACK);

    }

    @Override
    protected void drawCardContent(Canvas canvas, float margin, RectF frameRect) {
        drawWelcomeCard(canvas);
    }

    private void drawWelcomeCard(Canvas canvas) {

        final String title1 = "Planning";
        final String title2 = "Cards";

        paint.setStyle(Paint.Style.FILL);

        if (this.adjustedTextSize == null) {

            //get minimum allowed text size:
            //float minTextSize = adjustTextSize(text, canvas, .05f);

            //this.adjustedTextSize = adjustTextSize(text, canvas, .3f);

            //calc max scale factor by percent but limit to frameRect
            this.adjustedTextSize = adjustWelcomeTextSize(title1, canvas, 0.5f);
            //Log.d("drawview", "adjustWelcomeTextSize called");
        }

        paint.setTextSize(this.adjustedTextSize);

        //draw number centered
        paint.setTextAlign(Align.LEFT);
        Rect textBounds = new Rect();
        paint.getTextBounds(title1, 0, title1.length(), textBounds);
        float textCenterX = ((canvas.getWidth() - textBounds.width()) / 2f) - textBounds.left;  //must subtract left which is the hor. offset
        //float centerY = ((canvas.getHeight() - textBounds.height()) / 2f) - textBounds.bottom;  //must subtract bottom which is the fonts descent
        float centerY = ((canvas.getHeight() - textBounds.height()) * 0.39f) - textBounds.bottom;  //must subtract bottom which is the fonts descent
        float textCenterY = centerY + textBounds.height(); //bottom origin
        canvas.drawText(title1, textCenterX, textCenterY, paint);

        paint.getTextBounds(title2, 0, title2.length(), textBounds);
        textCenterX = ((canvas.getWidth() - textBounds.width()) / 2f) - textBounds.left;  //must subtract left which is the hor. offset
        textCenterY += textBounds.height() * 1.25f;
        canvas.drawText(title2, textCenterX, textCenterY, paint);
    }

    private float adjustWelcomeTextSize(String text, Canvas canvas, float widthPercent) {
        paint.setTextSize(100);
        paint.setTextScaleX(1.0f);
        Rect bounds = new Rect();
        // ask the paint for the bounding rect if it were to draw this
        // text
        paint.getTextBounds(text, 0, text.length(), bounds);

        // get the width that would have been produced
        int w = bounds.right - bounds.left;

        // make the text text up 70% of the height
        float target = (float) canvas.getWidth() * widthPercent;

        // figure out what textSize setting would create that height
        // of text
        float size = ((target / w) * 100f);

        // and set it into the paint
        paint.setTextSize(size);

        return size;
    }

}
