package de.mposchmann.planningcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public abstract class AbstractCardView extends View {

    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean hide = false;
    final float roundCornerPercentOfHeight = 0.04f;
    final int cardPadding = 1;

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
        invalidate();
    }

    public AbstractCardView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        this.setBackgroundColor(Color.BLACK);
    }

    protected abstract void drawCardContent(Canvas canvas, float margin, RectF frameRect);

    @Override
    public void onDraw(Canvas canvas) {
        drawCard(canvas);
    }

    private void drawCard(Canvas canvas) {
/*
        if (hide) {
            return;
        }
*/

        float roundCornerRadius = this.getHeight() * roundCornerPercentOfHeight;
        //margin a bit smaller than the round corner radius
        float margin = cardPadding + (roundCornerRadius * 0.9f);
        RectF frameRect = new RectF(margin, margin, this.getWidth() - 1 - margin, this.getHeight() - 1 - margin);

        drawEmptyCard(canvas, roundCornerRadius, frameRect);


        if (hide) {
            return;
        }

        drawCardContent(canvas, margin, frameRect);

    }

    private void drawEmptyCard(Canvas canvas, float roundCornerRadius, RectF frameRect) {

        this.setBackgroundColor(Color.BLACK);

        if (hide) {
            return;
        }

        //draw white card
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1f);
        RectF rect = new RectF(cardPadding, cardPadding, this.getWidth() - 1 - cardPadding, this.getHeight() - 1 - cardPadding);
        canvas.drawRoundRect(rect, roundCornerRadius,roundCornerRadius,  paint);

        //draw frame
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1f);

        //this round corner radius is smaller,  * 0.7 = hack, should calculated from padding
        canvas.drawRoundRect(frameRect, roundCornerRadius * 0.7f, roundCornerRadius * 0.7f, paint);
    }


}
