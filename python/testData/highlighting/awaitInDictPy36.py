async def f11(x):
    y = {await<error descr="Expression expected">:</error> 10 for <error descr="Cannot assign to await expression">await</error><error descr="Expression expected"> </error>in []}  # fail
    await x


def f12(x):
    y = {await: 10 for await in []}
    return x


async def f21(x):
    y = {mapper(await<error descr="Expression expected">)</error>: 10 for <error descr="Cannot assign to await expression">await</error><error descr="Expression expected"> </error>in []}  # fail
    await x


def f22(x):
    y = {mapper(await): 10 for await in []}
    return x


async def f31(x):
    <error descr="Cannot assign to await expression">await</error><error descr="Expression expected"> </error>= []  # fail
    y = {i: 10 for i in await<error descr="Expression expected">}</error>  # fail
    await x


def f32(x):
    await = []
    y = {i: 10 for i in await}
    return x


async def f41(x):
    y = {await z: 10 for z in []}
    await x


async def f42(x):
    y = {mapper(await z): 10 for z in []}
    await x


async def f43(x):
    y = {z: 10 for <error descr="Cannot assign to await expression">await z</error> in []}  # fail
    await x


async def f44(x):
    y = {z: 10 for z in await x}
    await x


async def f51():
    <error descr="Cannot assign to await expression">await</error><error descr="Expression expected"> </error>= 5  # fail
    return {await<error descr="Expression expected">:</error> 10}  # fail


def f52():
    await = 5
    return {await: 10}


async def f61():
    <error descr="Cannot assign to await expression">await</error><error descr="Expression expected"> </error>= 5  # fail
    return {"a": 10, await<error descr="Expression expected">:</error> 10, "b": 10}  # fail


def f62():
    await = 5
    return {"a": 10, await: 10, "b": 10}


async def f71(x):
    return {await x: 10}


async def f72(x):
    return {"a": 10, await x: 10, "b": 10}

async def f81(x):
    {fun: await fun() for fun in funcs if await smth}
    {fun: await fun() async for fun in funcs if await smth}