uniform mat4 u_MVP;
uniform mat3 u_NORMAL;

uniform vec3 u_LIGHT;

uniform mat4 worldMatrix;
uniform mat4 projectionMatrix;

#if __VERSION__ < 130
attribute vec3 in_Position;
attribute vec3 in_Normal;
attribute vec2 in_TVerts;

varying float v_Shade;
#else
#if __VERSION__ < 330
in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TVerts;
#else
layout(location = 0) in vec3 in_Position;
layout(location = 1) in vec3 in_Normal;
layout(location = 2) in vec2 in_TVerts;


#endif

out vec2 outTexCoord;
out float v_Shade;
#endif

void main() {
    vec3 normal = normalize(u_NORMAL * in_Normal);
    v_Shade = max(dot(normal, u_LIGHT), 0.0);
    gl_Position = u_MVP * vec4(in_Position, 1.0);

    //    gl_Position = projectionMatrix * worldMatrix * vec4(in_Position, 1.0);
    //    outTexCoord = in_TVerts;
}