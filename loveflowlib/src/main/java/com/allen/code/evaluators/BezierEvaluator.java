package com.allen.code.evaluators;


import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by allen on 2017/6/23.<br>
 * mail：1059709131@qq.com
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {

    private  PointF point1;
    private  PointF point2;

    public BezierEvaluator(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;

    }

    @Override
    public PointF evaluate(float t, PointF point0, PointF point3) {

        PointF point = new PointF();
        point.x = point0.x*(1-t)*(1-t)*(1-t)+3*point1.x*t*(1-t)*(1-t)+3*point2.x*t*t*(1-t)+point3.x*t*t*t;
        point.y = point0.y*(1-t)*(1-t)*(1-t)+3*point1.y*t*(1-t)*(1-t)+3*point2.y*t*t*(1-t)+point3.y*t*t*t;

        return point;
    }
}
