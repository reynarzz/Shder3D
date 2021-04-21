#shader VERTEX

#version 330 core

layout(location = 0) in vec4 _pos;
layout(location = 1) in vec2 _uvIn;

out vec2 _uv;

uniform mat4 _mvp_;
out vec3 _pixelPos;

void main()
{
	_uv = _uvIn - 0.5;
	_pixelPos = _pos.xyz;
	gl_Position = _mvp_ * _pos;
}

	#shader FRAGMENT

	#version 330 core

in vec2 _uv;
uniform vec3 _diffuse_;
out vec4 color;
uniform vec3 _cameraWorldPos_;
in vec3 _pixelPos;

void main()
{
	float maxDist = 100;

	float alpha = (maxDist - length(_pixelPos - _cameraWorldPos_));

	float thickness = 0.01;
	float spacing = 1.;

	if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
	{
		if(round(_pixelPos.z) == 0)
		{
			color = vec4(1.0, 0., 0., 0.7);
		}
		else if(round(_pixelPos.x) == 0)
		{
			color = vec4(0., 0.2, 1., 0.9);
		}
		else
		{
			color = vec4(vec3(1.), clamp(alpha, 0.0, 0.2));
		}
	}
	else
	{

		discard;
		color = vec4(0.);
	}
}

