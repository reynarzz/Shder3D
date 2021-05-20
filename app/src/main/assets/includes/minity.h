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
uniform vec4 _ProjectionParams;//done
uniform vec4 _ZBufferParams;//done

uniform vec4 _WorldSpaceLightPos0;
uniform vec4 _LightColor0;

uniform vec4 _Time;//done
uniform vec4 unity_DeltaTime;//done
uniform sampler2D _SHADOWMAP;

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

vec3 lerp(vec3 a, vec3 b, float t)
{
	if(t > 1.)
	{
		t = 1.;
	}

	return  vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
}

vec3 lerp(vec4 a, vec4 b, float t)
{
	if(t > 1.)
	{
		t = 1.;
	}

	return  vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
}

float LinearDepth(float d,float zNear,float zFar)
{
   return (2.0 * zNear) / (zFar + zNear - d * (zFar - zNear));
}

float shadow(vec4 lpos)
{
  vec3 proj = lpos.xyz/lpos.w;
  proj = proj*0.5+0.5;
  float closestD = texture2D(_SHADOWMAP, proj.xy).r;
  float current = proj.z;

  float shadow = 0.;

   if(current-0.004 > closestD)
   {
   shadow = 1.;
   }
   else
   {
   shadow = 0.;
   }

  if(proj.z > 1.0)
      shadow = 0.0;

  return shadow;

}
