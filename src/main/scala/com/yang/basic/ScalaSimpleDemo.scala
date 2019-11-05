package com.yang.basic

import scala.util.matching.Regex

object ScalaSimpleDemo {
  def main(args: Array[String]): Unit = {
    // 1
    val person = new People("a", 10, "male")
    person.eat()
    
    // 2
    val bar = Bar("foo")
    println(bar.foo)
    bar.bar()
    
    // 3
    val cat = new Cat("mimi")
    println(cat.name)
    
    // 4
    val iterator = new IntIterator(5)
    while (iterator.hasNext) {
      println(iterator.next)
    }
    
    // 5
    val richStringIterator = new RichStringIterator("abcdefg")
    richStringIterator.foreach(println)
    
    // 6
    val someEmail = Email("your father", "Fuck", "fuck your mother")
    val someSms = SMS("12345", "Are you there?")
    val someVoiceRecording = VoiceRecording("Tom", "voicerecording.org/id/123")
    println(new NotificationService().showNotification(someEmail))
    println(new NotificationService().showNotification(someSms))
    println(new NotificationService().showNotification(someVoiceRecording))

    // 7
    val domain = "pornhub.com"
    def getUrl = HighLevelFunc.urlBuilder(ssl = true, domain)
    val endpoint = "users"
    val query = "id=1"
    val url = getUrl(endpoint, query)
    println(url)
  }
}


// ========================== 类 ==========================================

/**
  * 普通 People 类
  * 类参数列表(主构造方法)中的参数不带 var, val 表示是私有的成员，只在类里能访问，不能通过对象直接访问
  * @param name: 姓名
  * @param age: 年龄
  */
class People(name: String, age: Int) {
  
  private var gender: String = _
  
  def this(name: String, age: Int, gender: String) = {
    this(name, age)
    this.gender = gender
  }
  
  def eat(): Unit = {
    println(name + " is eating")
  }
}


/**
  * 单例类
  * 伴生类
  * @param foo: e...
  */
class Bar(val foo: String) {
  import Bar._
  def bar(): Unit = sayBar(foo)
}

/**
  * 伴生对象
  * 伴生类和伴生对象之间可以互相访问其私有成员，使用伴生对象来定义那些在伴生类中不依赖于实例化对象而存在的成员变量或者方法
  * 单例对象中的 sayBar 方法对每一个实例化对象都是可见的
  */
object Bar {
  def apply(foo: String): Bar = new Bar(foo)
  
  private def sayBar(s: String): Unit = println(s)
}


class EmailConfig(username: String, domain: String)

/**
  * 伴生对象 EmailConfig 包含一个工厂方法 fromString 用来根据传入的 emailString 来 new EmailConfig 实例
  */
object EmailConfig {
  def fromString(emailString: String): Option[EmailConfig] = {
    emailString.split("@") match {
      case Array(a, b) => Some(new EmailConfig(a, b))
      case _ => None
    }
  }
}


// ========================== 抽象类 & 特质 =================================

/**
  * 特质: 可以定义抽象方法与成员
  */
trait Pet {val name: String}
class Cat(val name: String) extends Pet

trait Iterator[T] {
  def hasNext: Boolean
  def next: T
}
class IntIterator(to: Int) extends Iterator[Int] {
  private var current = 0
  override def hasNext: Boolean = current < to
  override def next: Int = {
    if (hasNext) {
      val tmp = current
      current += 1
      tmp
    } else 0
  }
}

/**
  * 定义抽象类
  */
abstract class AbsIterator {
  type T
  def hasNext: Boolean
  def next: T
}

/**
  * 特质可以继承抽象类，可以不实现抽象类的方法
  * 这里实现了 foreach 的特质
  */
trait RichIterator extends AbsIterator {
  /**
    * 接收一个函数作为参数，迭代作用在序列的元素上
    * @param f: 函数在 scala 里的类型表达式为 入参 => 返回
    *         无参数-无返回: () => ()
    *         无参数-有返回: () => SomeType
    *         有参数(单个)-无返回: SomeType => ()
    *         有参数(多个)-无返回: (SomeTypeA, SomeTypeB) => ()
    */
  def foreach(f: T => Unit): Unit = while (hasNext) f(next)
}

/**
  * 定义具体的类继承抽象类，需要实现定义的方法
  * @param s: 字符串参数
  */
class StringIterator(s: String = "") extends AbsIterator {
  override type T = Char
  private var i = 0
  override def hasNext: Boolean = i < s.length
  override def next: T = {
    val ch = s.charAt(i)
    i += 1
    ch
  }
}

/**
  * 把 StringIterator 和 RichIterator 中的功能组合成一个类 RichStringIterator
  */
class RichStringIterator(s: String) extends StringIterator(s) with RichIterator


// ========================== 案例类 & 模式匹配 =================================

/**
  * Notification 是一个虚基类，它有三个具体的子类 Email, SMS 和 VoiceRecording
  */
abstract class Notification
case class Email(sender: String, title: String, body: String) extends Notification
case class SMS(caller: String, message: String) extends Notification
case class VoiceRecording(contactName: String, link: String) extends Notification

class NotificationService {
  /**
    * 通过上面的 case class, 匹配 notification 对象
    * @param notification:
    * @return
    */
  def showNotification(notification: Notification): String = {
    notification match {
      case Email(email, title, _) => s"You got an email from $email with title: $title"
      case SMS(number, message) => s"You got an SMS from $number! Message: $message"
      case VoiceRecording(name, link) => s"you received a Voice Recording from $name! Click the link to hear it: $link"
    }
  }

  /**
    * 模式守卫，在模式后面加上 if ... 做更多具体限制
    * @param notification:
    * @param importantPeopleInfo:
    * @return
    */
  def showImportantNotification(notification: Notification, importantPeopleInfo: Seq[String]): String = {
    notification match {
      case Email(sender, _, _) if importantPeopleInfo.contains(sender) => "You got an email from special someone!"
      case SMS(number, _) if importantPeopleInfo.contains(number) => "You got an SMS from special someone!"
      case n: Notification => showNotification(n)
    }
  }
}


// ================================ 高阶函数 =================================
object HighLevelFunc {
  /**
    * 函数作为返回
    * @param ssl:
    * @param domain:
    * @return
    */
  def urlBuilder(ssl: Boolean, domain: String): (String, String) => String = {
    val schema = if (ssl) "https" else "http"
    (endpoint: String, query: String) => s"$schema://$domain/$endpoint?$query"
  }
}

// ================================ 正则表达式 ================================
object RegexDemo {
  val numberPattern: Regex = "[0-9]".r

}
