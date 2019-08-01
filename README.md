# QQStepView


	<!--自定义属性-->
	<declare-styleable name="QQStepView">
		<attr name="outerColor" format="color"/>
		<attr name="borderWidth" format="dimension"/>
		<attr name="innerColor" format="color"/>
		<attr name="stepTextSize" format="dimension"/>
		<attr name="stepTextColor" format="color"/>
	</declare-styleable>
	
	
	//代码中使用
	  final QQStepView stepView = findViewById(R.id.step_view);
        stepView.setmStepMax(4000);
        //属性动画
       ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0,3000);
       valueAnimator.setDuration(1000);
       //插值器
        valueAnimator.setInterpolator(new DecelerateInterpolator());
       valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
               float currentStep = (float) animation.getAnimatedValue();
               stepView.setmCureenStep((int) currentStep);
           }
       });
       //启动动画
       valueAnimator.start();
