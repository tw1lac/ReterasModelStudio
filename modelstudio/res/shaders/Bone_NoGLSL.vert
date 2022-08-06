#version 330 core

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_normal;
//layout (location = 2) in vec2 a_uv;
//layout (location = 3) in vec4 a_tangent;
layout (location = 2) in float a_selectionStatus;

uniform vec3 u_lightDirection;

uniform vec3 u_viewPos;
uniform mat4 u_projection;
uniform mat4 u_view;

uniform mat4 u_uvTransform;
uniform vec4 u_geosetColor;

uniform int u_lightingEnabled;

uniform vec4 u_vertColors[4];

out vec4 v_color;
out vec4 v_normal;

void main() {
    gl_Position = u_projection * u_view * a_position;
    v_normal = u_projection * u_view * a_normal;
    v_color = u_vertColors[int(a_selectionStatus)];


    vec4 viewPos = vec4(0,0,1,0);
    vec4 faceNormal = u_view * vec4(a_normal.xyz, 0);
    float shadowThing = (3 + dot(faceNormal.xyzw, viewPos.xyzw))/4.0;
    v_color.xyz = v_color.xyz * shadowThing;
}