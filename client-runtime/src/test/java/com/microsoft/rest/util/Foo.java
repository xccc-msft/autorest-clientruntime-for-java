/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.microsoft.rest.serializer.JsonFlatten;

import java.util.List;
import java.util.Map;

/**
 * Class for testing serialization.
 */
@JsonFlatten
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type", defaultImpl = Foo.class)
@JsonTypeName("foo")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "foochild", value = FooChild.class)
})
public class Foo {
    @JsonProperty(value = "properties.bar")
    public String bar;
    @JsonProperty(value = "properties.props.baz")
    public List<String> baz;
    @JsonProperty(value = "properties.props.q.qux")
    public Map<String, String> qux;
    @JsonProperty(value = "props.empty")
    public Integer empty;
    @JsonProperty(value = "")
    public Map<String, Object> additionalProperties;
}