/**
 * Copyright (C) 2014 MadMusic4001
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madmusic4001.dungeonmapper.view.views;

import android.opengl.GLES20;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 9/3/2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class riGraphicTools {
	// Program variables
	public static int sp_SolidColor;
	public static int sp_Image;

	/* SHADER Solid
	 *
	 * This shader is for rendering a colored primitive.
	 *
	 */
	public static final String vs_SolidColor =
			"uniform    mat4        uMVPMatrix;" +
					"attribute  vec4        vPosition;" +
					"void main() {" +
					"  gl_Position = uMVPMatrix * vPosition;" +
					"}";
	public static final String fs_SolidColor =
			"precision mediump float;" +
					"void main() {" +
					"  gl_FragColor = vec4(0.5,0,0,1);" +
					"}";
	public static final String vs_Image      =
			"uniform mat4 uMVPMatrix;" +
					"attribute vec4 vPosition;" +
					"attribute vec2 a_texCoord;" +
					"varying vec2 v_texCoord;" +
					"void main() {" +
					"  gl_Position = uMVPMatrix * vPosition;" +
					"  v_texCoord = a_texCoord;" +
					"}";
	public static final String fs_Image      =
			"precision mediump float;" +
					"varying vec2 v_texCoord;" +
					"uniform sampler2D s_texture;" +
					"void main() {" +
					"  gl_FragColor = texture2D( s_texture, v_texCoord);" +
					"}";

	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		// return the shader
		return shader;
	}
}
