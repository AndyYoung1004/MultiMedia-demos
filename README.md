"# MultiMedia-demos" 
"包含所有常用的多媒体demo"

OpenGL的操作顺序
1、编译顶点着色器和片源着色器代码，得到programId
2、使用programId去获取顶点坐标、纹理坐标、图片纹理的handle
3、将图片绑定一个textureId
4、GLES绘制工作
	4.1：绘制的时候，顶点着色器只会执行顶点的次数，而片元着色器，光栅化产生多少片段渲染管线就会调用多少次
	4.2：gl_FragColor = texture2D(inputImageTexture, textureCoordinate); 这个是调用函数进行纹理贴图
	4.3：glDrawArrays 是通过这类接口触发的绘制
	
OpenGL内容
1、顶点坐标handle，纹理坐标handle，顶点矩阵handle，纹理矩阵handle，纹理handle	
顶点坐标handle：定义好的数组直接传入即可
纹理坐标handle：同上
顶点矩阵handle：mvp矩阵，自己定义，用来做窗口校准，这样不用手动改变顶点坐标，直接用矩阵实现缩放即可
纹理矩阵handle：没啥用，直接从texture.getTransformMatrix获取即可
纹理handle：也就是纹理从哪儿来的，拿到后再设置给渲染管线去贴图，如下代码所示
GLES20.glActiveTexture(GLES20.GL_TEXTURE1); //激活第一路纹理
GLES20.glUniform1i(uTextureSamplerLocation, 1); //纹理handle从第一路纹理获取内容
