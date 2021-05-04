uniform mat4 UNITY_MATRIX_MVP; //done
uniform mat4 UNITY_MATRIX_MV; //done
uniform mat4 UNITY_MATRIX_V; //done
uniform mat4 UNITY_MATRIX_P; //done
uniform mat4 UNITY_MATRIX_T_MV;
uniform mat4 UNITY_MATRIX_IT_MV;
uniform mat4 unity_ObjectToWorld; //done
uniform mat4 unity_WorldToObject;//done
uniform vec4 _ScreenParams; //done
uniform vec3 _WorldSpaceCameraPos;//done
uniform vec4 _ProjectionParams;
uniform vec4 _ZBufferParams;

uniform vec4 _WorldSpaceLightPos0;
uniform vec4 _LightColor0;

uniform vec4 _Time;
uniform vec4 unity_DeltaTime; //done

#define float4 vec4
#define float3 vec3
#define float2 vec2
#define float4x4 mat4
#define float3x3 mat3
#define float2x2 mat2

float Luminance (vec4 c)
{
    return c.r * 0.2126 + c.g * 0.7152 + c.b * 0.0722;
}

float Luminance (vec3 c)
{
    return c.r * 0.21 + c.g * 0.72 + c.b * 0.07;
}

// Z buffer to linear 0..1 depth
float Linear01Depth(float z)
{
    return 1. / (_ZBufferParams.x *z +_ZBufferParams.y);
}

// Z buffer to linear depth
float LinearEyeDepth(float z)
{
    return 1. / (_ZBufferParams.z * z + _ZBufferParams.w);
}

vec4 UnityObjectToClipPos(vec4 vertex)
{
    return UNITY_MATRIX_MVP * vertex;
}

vec4 UnityObjectToViewPos(vec4 vertex)
{
   return vec4(1.);
}

vec4 tex2D(sampler2D tex, vec2 uv) {
     return texture2D(tex, uv);
}


