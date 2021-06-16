uniform vec4 u_COLOR;

uniform sampler2D texture_sampler;

#if __VERSION__ < 130
varying float v_Shade;

#define out_Color gl_FragColor
#define fragColor gl_FragColor
#else
in float v_Shade;
in vec2 outTexCoord;

#if __VERSION__ < 330
out vec4 out_Color;
out vec4 fragColor;
#else
layout(location = 0) out vec4 out_Color;
layout(location = 1) out vec4 fragColor;
#endif
#endif

void main() {
    out_Color = vec4(u_COLOR.xyz * v_Shade, u_COLOR.w);
    //    fragColor = texture(texture_sampler, outTexCoord);
}