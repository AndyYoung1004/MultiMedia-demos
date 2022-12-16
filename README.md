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

激活纹理与绑定纹理的区别？
首先区分2个概念，纹理单元和纹理目标。
GPU中有多个纹理单元 texture unit，每个纹理单元都有GL_TEXTURE_1D、GL_TEXTURE_2D等textureTarget纹理目标接口
android目前默认是32个纹理单元，glActiveTexture默认激活纹理单元0。
纹理目标，是opengl通过genTexture生成的，它可以用来去生成一个SurfaceTexture，当一个纹理目标绑定到一个纹理单元
的时候，opengl所作用的就是当前活跃的纹理单元，并最终作用到绑定的纹理目标上面，这样surfacetexture对应的id会将
纹理送到opengl里面去，然后可以通过glTexImage2D、glTexParameteri等函数改变纹理目标的状态。

设置激活的纹理单元，也就是告诉OpengGL状态机当前使用哪个纹理单元，在进行图像采样时，会在这个纹理单元上进行。

在openGL中，存在一系列的texture unit，通过 glActiveTexture激活当前的texture unit，默认的unit是0。而当前的texture unit中存在多个texture target，例如GL_TEXTURE_2D, GL_TEXTURE_CUBEMAP。
通过glBindTexture将一个texture object绑定到当前激活的texture unit的texture target上。然后通过glTexImage2D, glTexParameteri等函数改变texture object的状态。
创建texture object的时候需要指定texture unit吗？
并不需要，无论当前是哪个texture unit，不影响创建texture object。创建好的texture object可以绑定到其他texture unit的texture target上使用。
创建texture object后(glCreateTexture)，第一次调用glBindTexture，决定了texture object的类型，比如调用的是glBindTexture(GL_TEXTURE_2D)，那么这个texture object就是一个2d texture，其内部状态被初始化为2d texture的状态，它不能再被bind到其他类型的texture target上，否则会产生运行时错误。
什么时候需要关心texture unit?
当使用多重纹理的时候，也就是说在shader里面要同时使用多于一个sampler的时候。通过glUniform1i将texture unit传给sampler，让sampler知道应该去哪个texture unit中获取texture object，那么应该获取哪个texture target指向的texture object呢？这就要看sampler的类型了。比如sampler2D，就会获取sampler被指向的texture unit中的GL_TEXTURE_2D texture targert。
总结：
openGL中纹理的状态分为texture unit和texture object包含的状态。texture unit的状态包括当前激活的unit，每个unit下面的各个target分别指向哪些texture object。texture object的状态包含type, texParam, format等等。什么时候需要调用glActiveTexture以及glBindTexture就要看状态是否会改变。对于shader来说，他可以访问所有的texture unit中指定的texture object。只要你告诉他每个sampler使用哪个unit就行。如果每个unit的内容指定后不需要改变，则即便shader使用了多个sampler也不需要来回切换unit的状态。当然更常见的是渲染完一个pass后，需要改变当前texture unit中某target中的texture object，也就是需要换贴图了。那么标准的操作就是先glActiveTexture，然后glBindTexture。当然如果你只使用unit0，则不需要调用glActiveTexture。


