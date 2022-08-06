#version 330 core
layout (lines) in;
//layout (points) in;
layout (triangle_strip, max_vertices = 48) out;


const float MAGNITUDE = 10;

const float stemTopSize = .4f;
const float stemBotSize = .15f;

struct quad {
    vec3 p0;
    vec3 p1;
    vec3 p2;
    vec3 p3;
    vec3 norm;
};
struct cube {
    vec3 frntRghtUppp;
    vec3 frntRghtDown;
    vec3 backRghtUppp;
    vec3 backRghtDown;
    vec3 frntLeftUppp;
    vec3 frntLeftDown;
    vec3 backLeftUppp;
    vec3 backLeftDown;
};
//struct cube2 {
//    quad uppp;
//    quad down;
//    quad frnt;
//    quad back;
//    quad rght;
//    quad left;
//};

cube boneMarker = cube(
    vec3( 1, -1,  1),
    vec3( 1, -1, -1),
    vec3(-1, -1,  1),
    vec3(-1, -1, -1),
    vec3( 1,  1,  1),
    vec3( 1,  1, -1),
    vec3(-1,  1,  1),
    vec3(-1,  1, -1));

quad backQuad = quad(
    vec3( 1,  1, -1),
    vec3(-1,  1, -1),
    vec3( 1,  1,  1),
    vec3(-1,  1,  1),
    vec3( 0,  1,  0));
quad frontQuad = quad(
    vec3(-1, -1, -1),
    vec3( 1, -1, -1),
    vec3(-1, -1,  1),
    vec3( 1, -1,  1),
    vec3( 0, -1,  0));
quad topQuad = quad(
    vec3(-1, -1,  1),
    vec3( 1, -1,  1),
    vec3(-1,  1,  1),
    vec3( 1,  1,  1),
    vec3( 0,  0,  1));
quad botQuad = quad(
    vec3(-1,  1, -1),
    vec3( 1,  1, -1),
    vec3(-1, -1, -1),
    vec3( 1, -1, -1),
    vec3( 0,  0, -1));
quad leftQuad = quad(
    vec3(-1,  1, -1),
    vec3(-1, -1, -1),
    vec3(-1,  1,  1),
    vec3(-1, -1,  1),
    vec3(-1,  0,  0));
quad rightQuad = quad(
    vec3( 1, -1, -1),
    vec3( 1,  1, -1),
    vec3( 1, -1,  1),
    vec3( 1,  1,  1),
    vec3( 1,  0,  0));

//vec3[4] pointsStemTop = {
//    vec3( 1, -1, 1).scale(stemTopSize),
//    vec3(-1, -1, 1).scale(stemTopSize),
//    vec3( 1,  1, 1).scale(stemTopSize),
//    vec3(-1,  1, 1).scale(stemTopSize)};
//
//vec3[4] pointsStemBot = {
//    vec3( 1, -1, -1).scale(stemBotSize),
//    vec3(-1, -1, -1).scale(stemBotSize),
//    vec3( 1,  1, -1).scale(stemBotSize),
//    vec3(-1,  1, -1).scale(stemBotSize)};

uniform mat4 u_projection;
uniform mat4 u_view;
uniform vec2 scale;

//uniform vec2 start;
//uniform vec2 end;

in VS_OUT {
    vec4 pos;
    vec4 rot;
    vec4 color;
} gs_in[];

out vec4 v_color;

void EmVert(vec4 vPos) {
    gl_Position = vPos;
    EmitVertex();
}

vec3 transform(vec4 quat, vec3 v) {
    float uvx = quat.y * v.z - quat.z * v.y;
    float uvy = quat.z * v.x - quat.x * v.z;
    float uvz = quat.x * v.y - quat.y * v.x;
    float uuvx = quat.y * uvz - quat.z * uvy;
    float uuvy = quat.z * uvx - quat.x * uvz;
    float uuvz = quat.x * uvy - quat.y * uvx;
    float w2 = quat.w * 2;

    float newX = v.x + (uvx * w2) + (uuvx * 2);
    float newY = v.y + (uvy * w2) + (uuvy * 2);
    float newZ = v.z + (uvz * w2) + (uuvz * 2);

    return vec3(newX, newY, newZ);
}

void DrawQuadAt(vec4 p1, quad q, vec4 rot) {
//    vec2 mag = vec2(MAGNITUDE, MAGNITUDE)*scale;

    EmVert(u_projection * (p1 + vec4(transform(rot, q.p0), 0.0)));    // 1:bottom-left
    EmVert(u_projection * (p1 + vec4(transform(rot, q.p1), 0.0)));    // 2:bottom-right
    EmVert(u_projection * (p1 + vec4(transform(rot, q.p2), 0.0)));    // 3:top-left
    EmVert(u_projection * (p1 + vec4(transform(rot, q.p3), 0.0)));    // 4:top-right

    EndPrimitive();
}

void DrawPoint(vec4 p1) {
    vec2 mag = vec2(MAGNITUDE, MAGNITUDE)*scale;

    EmVert(p1 + vec4(-mag.x, -mag.y, 0.0, 0.0));    // 1:bottom-left
    EmVert(p1 + vec4( mag.x, -mag.y, 0.0, 0.0));    // 2:bottom-right
    EmVert(p1 + vec4(-mag.x,  mag.y, 0.0, 0.0));    // 3:top-left
    EmVert(p1 + vec4( mag.x,  mag.y, 0.0, 0.0));    // 4:top-right

    EndPrimitive();
}
void DrawCube(vec4 p1, vec4 rot) {
    DrawQuadAt(p1, frontQuad, rot);
    DrawQuadAt(p1, backQuad, rot);
    DrawQuadAt(p1, topQuad, rot);
    DrawQuadAt(p1, botQuad, rot);
    DrawQuadAt(p1, rightQuad, rot);
    DrawQuadAt(p1, leftQuad, rot);
}

void main() {
    v_color = gs_in[0].color;
//    GenerateLine(gl_in[0].gl_Position, gl_in[1].gl_Position);
    //    DrawPoint(u_projection * gl_Position);
    //    DrawPoint(u_projection * gs_in[0].pos);
    DrawCube(gs_in[0].pos, gs_in[0].rot);
    v_color = gs_in[1].color;
    DrawCube(gs_in[1].pos, gs_in[1].rot);
}