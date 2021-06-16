uniform mat4 u_MVP;
uniform mat3 u_NORMAL;
//uniform mat3 u_TEX_COORDS;

uniform vec3 u_LIGHT;

layout(location = 0) in vec3 in_Position;
layout(location = 1) in vec3 in_Normal;
layout(location = 2) in vec2 in_TVerts;
layout(location = 3) in vec3 in_Tris;

out vec2 outTexCoord;

void main() {
    vec3 normal = normalize(u_NORMAL * in_Normal);
    //    v_Shade = max(dot(normal, u_LIGHT), 0.0);
    gl_Position = u_MVP * vec4(in_Position, 1.0);

    //    gl_Position = projectionMatrix * worldMatrix * vec4(in_Position, 1.0);
    outTexCoord = in_TVerts;
}