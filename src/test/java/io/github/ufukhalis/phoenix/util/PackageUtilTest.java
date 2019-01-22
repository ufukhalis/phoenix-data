package io.github.ufukhalis.phoenix.util;

import org.junit.Assert;
import org.junit.Test;

public class PackageUtilTest {

    @Test
    public void test_given_package_name_should_not_return_empty_list() {
        Assert.assertTrue(!PackageUtil.find("io.github.ufukhalis").isEmpty());
    }
}
