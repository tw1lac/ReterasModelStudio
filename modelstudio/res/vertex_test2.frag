uniform vec4 u_COLOR;

uniform sampler2D texture_sampler;
uniform int useColor;
uniform vec3 color;

//#define out_Color gl_FragColor
//#define fragColor gl_FragColor

in vec2 outTexCoord;
//layout(location = 0) in vec2 outTexCoord;


//layout(location = 0) out vec4 out_Color;
//layout(location = 1) out vec4 fragColor;
//layout(location = 0) out vec4 fragColor;
out vec4 fragColor;

void main() {
    //    out_Color = vec4(u_COLOR.xyz * v_Shade, u_COLOR.w);

    //    fragColor = texture(texture_sampler, outTexCoord);

    if (useColor == 1)
    {
        fragColor = vec4(color, 1);
    }
    else
    {
        fragColor = texture(texture_sampler, outTexCoord);
    }
}