
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
    return 1.0 / (_ZBufferParams.x * z + _ZBufferParams.y);
}

// Z buffer to linear depth
float LinearEyeDepth(float z)
{
    return 1.0 / (_ZBufferParams.z * z + _ZBufferParams.w);
}

vec4 UnityObjectToClipPos(vec4 pos)
{
    return UNITY_MATRIX_MVP * pos;
}

vec4 UnityObjectToViewPos(vec4 pos)
{
   return vec4(1.);
}
vec3 Lerp(vec3 a, vec3 b, float t)
{
	if(t > 1.)
	{
		t = 1.;
	}

	return  vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
}