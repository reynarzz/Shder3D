precision mediump float;
varying vec2 _uv;
uniform vec3 _diffuse_;

uniform vec3 _cameraWorldPos_;
varying vec3 _pixelPos;

void main()
{
    float maxDist = 100.;

    // float alpha = (maxDist - length(_pixelPos - _cameraWorldPos_));

    float thickness = 0.02;
    float spacing = 100.;

    if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
    {
        if(int(_pixelPos.z) == 0)
        {
            gl_FragColor = vec4(1.0, 0., 0., 0.7);
        }
        else if(int(_pixelPos.x) == 0)
        {
            gl_FragColor = vec4(0., 0.2, 1., 0.9);
        }
        else
        {
            // gl_FragColor = vec4(vec3(1.), clamp(alpha, 0.0, 0.2));
            gl_FragColor = vec4(vec3(1.),  0.2);
        }
    }
    else
    {
        discard;
        //gl_FragColor = vec4(1.);
    }
}