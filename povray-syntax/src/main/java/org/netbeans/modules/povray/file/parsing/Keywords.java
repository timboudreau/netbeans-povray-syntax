/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.povray.file.parsing;

/**
 *
 * @author tim
 */
public enum Keywords implements TokenType {

    aa_level, aa_threshold, abs, absorption, accuracy, acos, acosh, adaptive,
    adc_bailout, agate, agate_turb, all, all_intersections, alpha, altitude,
    always_sample, ambient, ambient_light, angle, aperture, append,
    arc_angle, area_light, array, asc, ascii, asin, asinh, assumed_gamma,
    atan, atan2, atanh, autostop, average, b_spline, background, bezier_spline,
    bicubic_patch, black_hole, blob, blue, blur_samples, bounded_by,
    box, boxed, bozo, $break, brick, brick_size, brightness, brilliance,
    bump_map, bump_size, bumps, camera, $case, caustics, ceil, cells, charset,
    checker, chr, circular, clipped_by, clock, clock_delta, clock_on,
    collect, color, color_map, colour, colour_map, component, composite,
    concat, cone, confidence, conic_sweep, conserve_energy, contained_by,
    control0, control1, coords, cos, cosh, count, crackle, crand, cube, cubic,
    cubic_spline, cubic_wave, cutaway_textures, cylinder, cylindrical, debug,
    declare, $default, defined, degrees, density, density_file, density_map,
    dents, df3, difference, diffuse, dimension_size, dimensions, direction,
    disc, dispersion, dispersion_samples, dist_exp, distance, div,
    double_illuminate, eccentricity, $else, emission, end, error, error_bound,
    evaluate, exp, expand_thresholds, exponent, exterior, extinction,
    face_indices, facets, fade_color, fade_colour, fade_distance, fade_power,
    falloff, falloff_angle, $false, fclose, file_exists, filter, final_clock,
    final_frame, finish, fisheye, flatness, flip, floor, focal_point, fog,
    fog_alt, fog_offset, fog_type, fopen, form, frame_number, frequency,
    fresnel, function, gather, gif, global_lights, global_settings, gradient,
    granite, gray, gray_threshold, green, height_field, hexagon, hf_gray_16,
    hierarchy, hypercomplex, hollow, $if, ifdef, iff, ifndef, image_height,
    image_map, image_pattern, image_width, include, initial_clock,
    initial_frame, inside, inside_vector, $int, interior, interior_texture,
    internal, interpolate, intersection, intervals, inverse, ior, irid,
    irid_wavelength, isosurface, jitter, jpeg, julia, julia_fractal, lambda,
    lathe, leopard, light_group, light_source, linear_spline, linear_sweep,
    ln, load_file, local, location, log, look_at, looks_like, low_error_factor,
    macro, magnet, major_radius, mandel, map_type, marble, material, material_map,
    matrix, max, max_extent, max_gradient, max_intersections, max_iteration,
    max_sample, max_trace, max_trace_level, media, media_attenuation,
    media_interaction, merge, mesh, mesh2, metallic, method, metric, min,
    min_extent, minimum_reuse, mod, mortar, natural_spline, nearest_count,
    no, no_bump_scale, no_image, no_reflection, no_shadow, noise_generator,
    normal, normal_indices, normal_map, normal_vectors, number_of_waves, object,
    octaves, off, offset, omega, omnimax, on, once, onion, open, orient,
    orientation, orthographic, panoramic, parallel, parametric, pass_through,
    pattern, perspective, pgm, phase, phong, phong_size, photons, pi, pigment,
    pigment_map, pigment_pattern, planar, plane, png, point_at, poly, poly_wave,
    polygon, pot, pow, ppm, precision, precompute, pretrace_end, pretrace_start,
    prism, prod, projected_through, pwr, quadratic_spline, quadric, quartic,
    quaternion, quick_color, quick_colour, quilted, radial, radians, radiosity,
    radius, rainbow, ramp_wave, rand, range, ratio, read, reciprocal,
    recursion_limit, red, reflection, reflection_exponent, refraction,
    render, repeat, rgb, rgbf, rgbft, rgbt, right, ripples, rotate, roughness,
    samples, save_file, scale, scallop_wave, scattering, seed, select,
    shadowless, sin, sine_wave, sinh, size, sky, sky_sphere, slice, slope,
    slope_map, smooth, smooth_triangle, solid, sor, spacing, specular, sphere,
    sphere_sweep, spherical, spiral1, spiral2, spline, split_union, spotlight,
    spotted, sqr, sqrt, statistics, str, strcmp, strength, strlen, strlwr,
    strupr, sturm, substr, sum, superellipsoid, $switch, sys, tan, tanh,
    target, text, texture, texture_list, texture_map, tga, thickness,
    threshold, tiff, tightness, tile2, tiles, tolerance, toroidal, torus,
    trace, transform, translate, transmit, triangle, triangle_wave, $true, ttf,
    turb_depth, turbulence, type, u_steps, ultra_wide_angle, undef, union, up,
    use_alpha, use_color, use_colour, use_index, utf8, uv_indices, uv_mapping,
    uv_vectors, v_steps, val, variance, vaxis_rotate, vcross, vdot, version,
    vertex_vectors, vlength, vnormalize, vrotate, vstr, vturbulence, warning,
    warp, water_level, waves, $while, width, wood, wrinkles, write;

    @Override
    public String toString() {
        if (name().startsWith("$")) {
            return name().substring(1);
        }
        return name();
    }

    public boolean matches(String s) {
        s = s.toLowerCase();
        return toString().equals(s);
    }

    public static Keywords match(String s) {
        s = s.trim();
        for (Keywords k : values()) {
            if (k.matches(s)) {
                return k;
            }
        }
        return null;
    }

    public boolean isVisualAttribute() {
        switch(this) {
            case texture :
            case pigment :
            case finish :
            case interior :
                return true;
            default :
                return false;
        }
    }

    public boolean isShape() {
        switch(this) {
            case sphere :
            case sphere_sweep :
            case box :
            case julia_fractal :
            case plane :
            case lathe :
            case bicubic_patch :
            case mesh :
            case prism :
            case cube :
            case blob :
            case conic_sweep :
            case polygon :
                return true;
            default :
                return false;
        }
    }

    public boolean occursAfterHash() {
        switch (this) {
            case end:
            case $if:
            case $while:
            case declare:
            case macro:
            case local:
            case ifdef:
            case ifndef:
            case $switch:
            case $default:
                return true;
            default :
                return false;

        }
    }
}
