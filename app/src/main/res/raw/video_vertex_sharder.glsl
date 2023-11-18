attribute vec4 aPosition;
attribute vec4 aTexCoord;
varying vec2 vTexCoord;
void main() {
    vTexCoord = aTexCoord.xy;
    gl_Position = aPosition;
}